package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.User;
import io.minio.*;
import io.minio.admin.MinioAdminClient;
import io.minio.admin.UserInfo;
import io.minio.messages.Item;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class MinioServiceImpl implements SimpleStorageService {

    private static final String BUCKET_NAME = "-private-bucket";

    @Override
    public Iterable<Result<Item>> getObjectsFromAllBuckets() {
        MinioClient client = getClient();
        return client.listObjects(ListObjectsArgs.builder()
                .delimiter("/")
                .recursive(true)
                .bucket(getUserDetails().getUsername().toLowerCase() + BUCKET_NAME)
                .build()
        );
    }


    private static MakeBucketArgs getMakeBucketArgs(UserDetails userDetails) {
        return MakeBucketArgs.builder()
                .bucket(userDetails.getUsername().toLowerCase() + BUCKET_NAME)
                .build();
    }

    private static BucketExistsArgs getBucketArgs(UserDetails userDetails) {
        return BucketExistsArgs.builder()
                .bucket(userDetails.getUsername().toLowerCase() + BUCKET_NAME)
                .build();
    }

    private ListObjectsArgs createListObjectsArgs(String bucketName) {
        return ListObjectsArgs.builder()
                .bucket(bucketName)
                .recursive(true)
                .build();
    }

    @Override
    public void uploadObject() {
        MinioClient client = getClient();
        validateClientBucket(client);
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket(getUserDetails().getUsername() + BUCKET_NAME)
//                .object()
                .build();
    }

    private void validateClientBucket(MinioClient client) {
        UserDetails userDetails = getUserDetails();
        BucketExistsArgs bucketArgs = getBucketArgs(userDetails);
        try {
            if (!client.bucketExists(bucketArgs)) {
                MakeBucketArgs makeBucketArgs = getMakeBucketArgs(userDetails);
                client.makeBucket(makeBucketArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteObject() {

    }

    @Override
    public void register(User user) {
        MinioAdminClient adminClient = getAdminClient();
        addNewUserWithPolicy(adminClient, user);
    }

    private MinioAdminClient getAdminClient() {
        return MinioAdminClient.builder()
                .endpoint("localhost", 9000, false)
                .credentials("minio", "12341234")
                .build();
    }

    private void addNewUserWithPolicy(MinioAdminClient adminClient, User user) {
        String policyJson = getPolicyJson(user);
        try {
            adminClient.addCannedPolicy(user.getUsername().toLowerCase(), policyJson);
            adminClient.addUser(user.getUsername(), UserInfo.Status.ENABLED, user.getPassword(),
                    user.getUsername().toLowerCase(), Arrays.asList("my-group"));
            adminClient.setPolicy(user.getUsername(), false, user.getUsername().toLowerCase());
        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException | InvalidCipherTextException e) {
            e.printStackTrace();
        }
    }

    private static String getPolicyJson(User user) {
        String policyJson = String.format(
                """
                        {
                          "Version": "2012-10-17",
                          "Statement": [
                            {
                              "Effect": "Allow",
                              "Principal": {
                                "AWS": [
                                  "*"
                                ]
                              },
                              "Action": [
                                "s3:GetBucketLocation",
                                "s3:ListBucket",
                                "s3:ListBucketMultipartUploads"
                              ],
                              "Resource": [
                                "arn:aws:s3:::%s"
                              ]
                            },
                            {
                              "Effect": "Allow",
                              "Principal": {
                                "AWS": [
                                  "*"
                                ]
                              },
                              "Action": [
                                "s3:AbortMultipartUpload",
                                "s3:DeleteObject",
                                "s3:GetObject",
                                "s3:ListMultipartUploadParts",
                                "s3:PutObject"
                              ],
                              "Resource": [
                                "arn:aws:s3:::%s/*"
                              ]
                            }
                          ]
                        }
                        """
                ,
                user.getUsername().toLowerCase() + BUCKET_NAME, user.getUsername().toLowerCase() + BUCKET_NAME
        );
        return policyJson;
    }

    private MinioClient getClient() {
        UserDetails userDetails = getUserDetails();
        return MinioClient.builder()
                .endpoint("localhost", 9000, false)
                .credentials(userDetails.getUsername(), userDetails.getPassword())
                .build();
    }

    private static UserDetails getUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

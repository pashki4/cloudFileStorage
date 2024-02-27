package dev.pasha.cloudfilestorage;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MinioService {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for(Map.Entry<String, String> entry : entries) {
            entry.getKey();
            entry.getValue();
        }

    }

    private static void listObjects(MinioClient client) {
        String bucketName = "newuser--private-bucket";
        Iterable<Result<Item>> results = client.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build()
        );
        results.forEach(result -> {
            try {
                Item item = result.get();
                System.out.println("Item: " + "n" +
                                   "item.etag() -> " + item.etag() + "n" +
                                   "item.objectName() -> " + item.objectName() + "n" +
                                   "item.storageClass() -> " + item.storageClass() + "n" +
                                   "item.versionId() -> " + item.versionId() + "n" +
                                   "item.isDir() -> " + item.isDir() + "n"
                );
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static void uploadObject(MinioClient client) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, URISyntaxException {
        String bucketName = "newUser-private-bucket";
//        String objectName = "/temp/1.knew";
        String fileName = "C:\\Users\\pasha\\Desktop\\test.cpp";
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket(bucketName)
//                .object(objectName)
                .filename(fileName)
                .build();

        client.uploadObject(uploadObjectArgs);
    }

    private static void showBucketList(MinioClient client) throws Exception {
        client.listBuckets().forEach(b -> System.out.println("Bucket: " + b.name() + " " + b.creationDate()));

    }

    private static void createBucket(MinioClient client) throws Exception {
        String bucketName = "newuser-private-bucket";

        client.makeBucket(MakeBucketArgs.builder()
                .bucket(bucketName)
                .build());
    }

    private static void addBucketPolicy(MinioClient client) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String policyJson = "{ \"Version\": \"2012-10-17\", \"Statement\": [{ \"Sid\": \"newUser-write-only\", \"Effect\": \"Allow\", \"Principal\": {\"AWS\": [\"minio:users/newUser\"]}, \"Action\": [\"s3:PutObject\", \"s3:ListBucketMultipartUploads\", \"s3:AbortMultipartUpload\"], \"Resource\": [\"arn:aws:s3:::newuser-private-bucket/*\"] }] }";
//                    """
//                    {
//                        "Version": "2012-10-17",
//                        "Statement ": [
//                           {
//                              "Sid": "newUser-write-only",
//                              "Effect": "Allow",
//                              "Principal": "AWS": "arn:aws:iam::your-account-id:user/newUser",
//                              "Action": [
//                                 "s3:PutObject",
//                                 "s3:ListBucketMultipartUploads",
//                                 "s3:AbortMultipartUpload"
//                              ],
//                              "Resource": "arn:aws:s3::newuser-private-bucket/*"
//                          }
//                    }
//                """;

        client.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket("newuser-private-bucket")
                .config(policyJson)
                .build());
    }

}

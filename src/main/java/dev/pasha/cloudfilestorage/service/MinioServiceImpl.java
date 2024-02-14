package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.User;
import io.minio.*;
import io.minio.admin.MinioAdminClient;
import io.minio.admin.UserInfo;
import io.minio.messages.Item;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinioServiceImpl implements SimpleStorageService {

    private static final String BUCKET_NAME_POSTFIX = "-private-bucket";

    @Override
    public Iterable<Result<Item>> getObjects() {
        MinioClient client = getClient();
        return client.listObjects(ListObjectsArgs.builder()
                .delimiter("/")
                .recursive(true)
                .bucket(getUserDetails().getUsername().toLowerCase() + BUCKET_NAME_POSTFIX)
                .build()
        );
    }


    @Override
    public void uploadObject(String fileUrl) throws Exception {
        MinioClient client = getClient();
        client.uploadObject(UploadObjectArgs.builder()
                        .bucket(getUserDetails().getUsername().toLowerCase() + BUCKET_NAME_POSTFIX)
                        .object("current_position + file_name")
                        .filename(fileUrl)
                .build());
    }

    @Override
    public void deleteObject() {

    }

    @Override
    public void renameObject() {

    }

    @Override
    public void register(User user) throws Exception {
        MinioAdminClient adminClient = getAdminClient();
        addNewReadWriteUser(adminClient, user);
        createUserBucket(user);
    }

    private void createUserBucket(User user) throws Exception {
        MinioClient client = MinioClient.builder()
                .endpoint("localhost", 9000, false)
                .credentials(user.getUsername(), user.getPassword())
                .build();
        //
        /*
        MINIO BUCKET-NAMING RULES:
            Bucket names must be between 3 (min) and 63 (max) characters long.
            Bucket names can consist only of lowercase letters, numbers, dots (.), and hyphens (-).
            Bucket names must not contain two adjacent periods, or a period adjacent to a hyphen.
            Bucket names must not be formatted as an IP address (for example, 192.168.5.4).
            Bucket names must not start with the prefix xn--.
            Bucket names must not end with the suffix -s3alias. This suffix is reserved for access point alias names.
            Bucket names must be unique within a partition.
         */
        if (!client.bucketExists(BucketExistsArgs.builder()
                .bucket(user.getUsername().toLowerCase() + BUCKET_NAME_POSTFIX)
                .build())
        ) {
            client.makeBucket(MakeBucketArgs.builder()
                    .bucket(user.getUsername().toLowerCase() + BUCKET_NAME_POSTFIX)
                    .build()
            );
        }
    }

    private MinioAdminClient getAdminClient() {
        return MinioAdminClient.builder()
                .endpoint("localhost", 9000, false)
                .credentials("minio", "12341234")
                .build();
    }

    private void addNewReadWriteUser(MinioAdminClient adminClient, User user) throws Exception {
        adminClient.addUser(user.getUsername(), UserInfo.Status.ENABLED, user.getPassword(),
                "readwrite", List.of("my-group"));
        adminClient.setPolicy(user.getUsername(), false, "readwrite");
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

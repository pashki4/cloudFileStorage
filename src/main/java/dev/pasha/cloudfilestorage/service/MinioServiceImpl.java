package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.CustomUserDetails;
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

    private static final String BUCKET_NAME = "user-files";
    private static final String USER_BUCKET_NAME = "user-%d-files";

    @Override
    public Iterable<Result<Item>> getObjectsByPath(String path) {
        MinioClient client = getUserClient();
        return getObjects(path, client);
    }

    private static Iterable<Result<Item>> getObjects(String path, MinioClient client) {
        String validatedPath = validatePath(path);
        return client.listObjects(ListObjectsArgs.builder()
                .delimiter("/")
                .bucket(BUCKET_NAME)
                .prefix(validatedPath)
                .recursive(false)
                .build());
    }

    private static String validatePath(String path) {
        if (path == null) {
            return String.format(USER_BUCKET_NAME + "/", getUserDetails().getId());
        } else if (!path.endsWith("/")) {
            return path + "/";
        }
        return path;
    }

    @Override
    public void uploadObject(String fileUrl) throws Exception {
        MinioClient client = getUserClient();
        client.uploadObject(UploadObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object("current_position + file_name")
                .filename("")
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
        createCommonBucketIfNotExists();
        createNewUser(user);
    }

    private void createNewUser(User user) throws Exception {
        addNewReadWriteUser(user);
    }

    private void createCommonBucketIfNotExists() throws Exception {
        MinioClient client = MinioClient.builder()
                .credentials("minio", "12341234")
                .endpoint("localhost", 9000, false)
                .build();
        if (!client.bucketExists(BucketExistsArgs.builder()
                .bucket("user-files")
                .build())) {
            client.makeBucket(MakeBucketArgs.builder()
                    .bucket(BUCKET_NAME)
                    .build());
        }
    }

    private void addNewReadWriteUser(User user) throws Exception {
        MinioAdminClient adminClient = getAdminClient();
        adminClient.addUser(user.getUsername(), UserInfo.Status.ENABLED, user.getPassword(),
                "readwrite", List.of("my-group"));
        adminClient.setPolicy(user.getUsername(), false, "readwrite");
    }

    private MinioAdminClient getAdminClient() {
        return MinioAdminClient.builder()
                .endpoint("localhost", 9000, false)
                .credentials("minio", "12341234")
                .build();
    }

    private MinioClient getUserClient() {
        UserDetails userDetails = getUserDetails();
        return MinioClient.builder()
                .endpoint("localhost", 9000, false)
                .credentials(userDetails.getUsername(), userDetails.getPassword())
                .build();
    }

    private static CustomUserDetails getUserDetails() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

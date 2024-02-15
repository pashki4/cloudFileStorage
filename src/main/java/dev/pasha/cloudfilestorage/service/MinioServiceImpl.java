package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.CustomUserDetails;
import dev.pasha.cloudfilestorage.model.User;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.UploadObjectArgs;
import io.minio.admin.MinioAdminClient;
import io.minio.admin.UserInfo;
import io.minio.messages.Item;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MinioServiceImpl implements SimpleStorageService {

    private static final String BUCKET_NAME = "user-files";
    private static final String USER_PREFIX_PATTERN = "user-%d-files";

    @Override
    public Iterable<Result<Item>> getObjects() {
        MinioClient client = getClient();
        Iterable<Result<Item>> iterable = client.listObjects(ListObjectsArgs.builder()
                .delimiter("/")
                .recursive(true)
                .bucket(BUCKET_NAME)
                .build());
        return filterUserDate(iterable);
    }

    @NotNull
    private static List<Result<Item>> filterUserDate(Iterable<Result<Item>> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .filter(result -> {
                    try {
                        return result.get().objectName().startsWith(String.format(USER_PREFIX_PATTERN, getUserDetails().getId()));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }


    @Override
    public void uploadObject(String fileUrl) throws Exception {
        MinioClient client = getClient();
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
        MinioAdminClient adminClient = getAdminClient();
        addNewReadWriteUser(adminClient, user);
    }

    private void addNewReadWriteUser(MinioAdminClient adminClient, User user) throws Exception {
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

    private MinioClient getClient() {
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

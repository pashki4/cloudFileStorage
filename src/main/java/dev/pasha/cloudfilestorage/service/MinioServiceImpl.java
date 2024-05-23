package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.exception.*;
import dev.pasha.cloudfilestorage.model.CustomUserDetails;
import dev.pasha.cloudfilestorage.model.ItemWrapper;
import dev.pasha.cloudfilestorage.model.User;
import io.minio.*;
import io.minio.admin.MinioAdminClient;
import io.minio.admin.UserInfo;
import io.minio.messages.Item;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
public class MinioServiceImpl implements SimpleStorageService {

    private static final String BUCKET_NAME = "user-files";
    private static final String USER_BUCKET_NAME = "user-%d-files";

    @Override
    public List<ItemWrapper> getObjectsByPath(String path) {
        MinioClient client = getUserClient();
        String validatedPath = validatePath(path);
        Iterable<Result<Item>> objects = client.listObjects(ListObjectsArgs.builder()
                .delimiter("/")
                .bucket(BUCKET_NAME)
                .prefix(validatedPath)
                .recursive(false)
                .build());
        return StreamSupport.stream(objects.spliterator(), false)
                .map(result -> {
                    try {
                        return result.get();
                    } catch (Exception e) {
                        throw new GetMinioObjectException("Error loading objects by path: " + path, e);
                    }
                })
                .map(ItemWrapper::new)
                .toList();
    }

    private static String validatePath(String path) {
        String root = String.format(USER_BUCKET_NAME + "/", getUserDetails().getId());
        if (path == null || path.isBlank()) {
            return root;
        } else if (!path.endsWith("/")) {
            return root + path + "/";
        }
        return root + path;
    }

    @Override
    public List<ItemWrapper> searchObjectByQuery(String query) {
        MinioClient client = getUserClient();
        Iterable<Result<Item>> objects = client.listObjects(ListObjectsArgs.builder()
                .bucket(BUCKET_NAME)
                .prefix(String.format(USER_BUCKET_NAME, getUserDetails().getId()))
                .recursive(true)
                .maxKeys(100)
                .build());
        return StreamSupport.stream(objects.spliterator(), false)
                .map(result -> {
                    try {
                        return result.get();
                    } catch (Exception e) {
                        throw new GetMinioObjectException("Error search objects by query: " + query, e);
                    }
                })
                .filter(item -> item.objectName().toLowerCase().contains(query.toLowerCase()))
                .map(ItemWrapper::new)
                .toList();
    }

    @Override
    public void putObject(String path, MultipartFile multiPartFile) {
        String fullObjectName = getFullObjectName(path, multiPartFile);
        MinioClient client = getUserClient();
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(fullObjectName)
                    .contentType(multiPartFile.getContentType())
                    .stream(multiPartFile.getInputStream(), multiPartFile.getSize(), -1)
                    .build());
        } catch (Exception e) {
            throw new PutMinioObjectException("Error putting object: " + multiPartFile.getOriginalFilename(), e);
        }
    }

    @NotNull
    private static String getFullObjectName(String path, MultipartFile multipartFile) {

        return String.format(USER_BUCKET_NAME + "/", getUserDetails().getId()) + path + "/" + multipartFile.getOriginalFilename();
    }

    @Override
    public void deleteObject(String name) {
        MinioClient client = getUserClient();
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(String.format(USER_BUCKET_NAME + "/", getUserDetails().getId()) + name)
                    .build());
        } catch (Exception e) {
            throw new DeleteMinioObjectException("Error deleting object: " + name, e);
        }
    }

    @Override
    public void renameObject(String oldName, String newName) {
        MinioClient userClient = getUserClient();
        String fullPathNewName = createFullPathWithNewName(oldName, newName);
        //TODO get full url from oldName and add it to newName -> redirect:/?path=url
        try {
            userClient.copyObject(CopyObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(String.format(USER_BUCKET_NAME + "/", getUserDetails().getId()) + fullPathNewName)
                    .source(CopySource.builder()
                            .bucket(BUCKET_NAME)
                            .object(String.format(USER_BUCKET_NAME + "/", getUserDetails().getId()) + oldName)
                            .build())
                    .build());
        } catch (Exception e) {
            throw new RenameMinioObjectException("Error renaming object: " + oldName, e);
        }
    }


    private static String createFullPathWithNewName(String query, String newName) {
        if (query.contains("/")) {
            String url = query.substring(0, query.lastIndexOf("/"));
            return url + "/" + newName;
        }
        return newName;
    }

    @Override
    public void createUser(User user) {
        createCommonBucketIfNotExists();
        createNewUser(user);
    }

    private void createNewUser(User user) {
        try {
            createUserWithReadWritePolicy(user);
        } catch (Exception e) {
            throw new CreateMinioUserException("Error creating user: " + user, e);
        }
    }

    @Override
    public Map<String, String> createBreadCrumb(String path) {
        Map<String, String> result = new LinkedHashMap<>();
        if (path == null || path.isBlank()) {
            return result;
        }
        String[] split = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            String currentElement = split[i];
            for (int j = 0; j < i; j++) {
                sb.append(split[j].toLowerCase()).append("/");
            }
            sb.append(currentElement).append("/");
            result.put(currentElement, sb.toString());
        }
        String lastElement = split[split.length - 1];
        result.put(lastElement, sb.append(lastElement).toString());
        return result;
    }

    private void createCommonBucketIfNotExists() {
        MinioClient client = MinioClient.builder()
                .credentials("minio", "12341234")
                .endpoint("localhost", 9000, false)
                .build();
        try {
            if (!client.bucketExists(BucketExistsArgs.builder()
                    .bucket("user-files")
                    .build())) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(BUCKET_NAME)
                        .build());
            }
        } catch (Exception e) {
            throw new CreateMinioBucketException("Error creating bucket: " + BUCKET_NAME, e);
        }
    }

    private void createUserWithReadWritePolicy(User user) throws Exception {
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

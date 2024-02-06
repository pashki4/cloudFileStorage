package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.User;
import io.minio.MinioClient;
import io.minio.admin.MinioAdminClient;
import io.minio.admin.UserInfo;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class MinioServiceImpl implements SimpleStorageService {

    @Override
    public void uploadObject() {
        MinioClient client = getClient();
    }

    @Override
    public void deleteObject() {

    }

    @Override
    public void register(User user) {
        MinioAdminClient adminClient = getAdminClient();
        addNewReadWriteUser(adminClient, user);
    }

    private MinioAdminClient getAdminClient() {
        return MinioAdminClient.builder()
                .endpoint("localhost", 9000, false)
                .credentials("minio", "12341234")
                .build();
    }

    private MinioClient getClient() {
        return MinioClient.builder()
                .endpoint("localhost", 9000, false)
                .build();
    }

    private void addNewReadWriteUser(MinioAdminClient adminClient, User user) {
        try {
            adminClient.addUser(user.getUsername(), UserInfo.Status.ENABLED, user.getPassword(),
                    "readwrite", Arrays.asList("my-group"));
            adminClient.setPolicy(user.getUsername(), false, "readwrite");
        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException | InvalidCipherTextException e) {
            e.printStackTrace();
        }
    }
}

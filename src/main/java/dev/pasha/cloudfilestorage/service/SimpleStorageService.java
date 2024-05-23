package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.ItemWrapper;
import dev.pasha.cloudfilestorage.model.User;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface SimpleStorageService {

    List<ItemWrapper> getObjectsByPath(String path);

    List<ItemWrapper> searchObjectByQuery(String query);

    void putObject(String path, MultipartFile multipartFile);

    void deleteObject(String name);

    void renameObject(String oldName, String newName);

    void createUser(User user) throws Exception;

    Map<String, String> createBreadCrumb(String path);
}

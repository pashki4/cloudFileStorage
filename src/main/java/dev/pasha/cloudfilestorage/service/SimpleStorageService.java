package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.MinioItemWrapper;
import dev.pasha.cloudfilestorage.model.MinioObject;
import dev.pasha.cloudfilestorage.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface SimpleStorageService {

    List<MinioItemWrapper> getObjectsByPath(String path);

    void putObject(String path, MultipartFile multipartFile);

    void deleteObject(String name);

    void renameObject(String oldName, String newName);

    void createUser(User user) throws Exception;

    Map<String, String> createBreadCrumb(String path);
}

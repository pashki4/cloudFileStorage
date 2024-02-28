package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.MinioItemWrapper;
import dev.pasha.cloudfilestorage.model.User;

import java.util.List;
import java.util.Map;

public interface SimpleStorageService {

    List<MinioItemWrapper> getObjectsByPath(String path);

    void uploadObject(String fileUrl) throws Exception;

    void deleteObject();

    void renameObject();

    void register(User user) throws Exception;

    Map<String, String> createBreadCrumb(String path);
}

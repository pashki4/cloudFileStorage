package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.User;
import io.minio.Result;
import io.minio.messages.Item;

public interface SimpleStorageService {

    Iterable<Result<Item>> getObjectsByPath(String path);

    void uploadObject(String fileUrl) throws Exception;

    void deleteObject();

    void renameObject();

    void register(User user) throws Exception;

}

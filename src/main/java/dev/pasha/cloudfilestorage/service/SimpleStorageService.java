package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.User;
import io.minio.Result;
import io.minio.messages.Item;

public interface SimpleStorageService {

    Iterable<Result<Item>> getObjectsFromAllBuckets();

    void uploadObject();

    void deleteObject();

    void register(User user);

}

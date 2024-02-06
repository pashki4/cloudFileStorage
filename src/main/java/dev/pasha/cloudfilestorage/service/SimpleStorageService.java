package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.User;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface SimpleStorageService {

    void uploadObject();

    void deleteObject();

    void register(User user);
}

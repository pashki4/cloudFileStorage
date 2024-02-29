package dev.pasha.cloudfilestorage;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MinioService {

    public static void main(String[] args) throws Exception {
        MinioClient client = MinioClient.builder()
                .endpoint("localhost", 9000, false)
                .credentials("newUser2", "$2a$10$sMpIe/0apFRCkdKT16nyY.oeG8iAaP5ExHtsiJgwJQfxnWR6jGqtO")
                .build();
        uploadObject(client);

    }

    private static void uploadObject(MinioClient client) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, URISyntaxException {
        String bucketName = "user-files";
        String root = "user-7-files/";
        String objectName = "/temp/frog3.jpg";
//        String fileName = "C:\\Users\\pasha\\Desktop\\1. 1 guard (3mm) - lever half way o.txt";
        String fileUrl = "C:\\Users\\pasha\\Desktop\\frog3.jpg";
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket(bucketName)
                .filename(fileUrl)
                .object(root + objectName)
                .build();

        client.uploadObject(uploadObjectArgs);
    }

}

package dev.pasha.cloudfilestorage;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioService {

    public static void main(String[] args) throws Exception {
        MinioClient newUser = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("newUser", "12341234")
                .build();

        newUser.listBuckets().forEach(System.out::println);

    }

    private static void listObjects(MinioClient client) {
        String bucketName = "new-user-bucket";
        ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                .bucket(bucketName)
                .recursive(true)
                .build();
        Iterable<Result<Item>> results = client.listObjects(listObjectsArgs);
        results.forEach(result -> {
            try {
                Item item = result.get();
                System.out.println("Item: " + "\n" +
                                   "item.etag() -> " + item.etag() + "\n" +
                                   "item.objectName() -> " + item.objectName() + "\n" +
                                   "item.storageClass() -> " + item.storageClass() + "\n" +
                                   "item.versionId() -> " + item.versionId() + "\n" +
                                   "item.isDir() -> " + item.isDir() + "\n"
                );
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static void uploadObject(MinioClient client) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, URISyntaxException {
        String bucketName = "new-user-bucket";
        String objectName = "/temp/1.knew";
//        String contentType = "";
        String fileName = "C:\\Users\\pasha\\Desktop\\file1.weba";
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(fileName)
//                .contentType(contentType)
                .build();

        client.uploadObject(uploadObjectArgs);
    }

    private static void showBucketList(MinioClient client) throws Exception {
        client.listBuckets().forEach(b -> System.out.println("Bucket: " + b.name() + " " + b.creationDate()));

    }

    private static void createBucket(MinioClient client) throws Exception {
        String bucketName = "java-demo-bucket";
        MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                .bucket(bucketName)
                .build();

        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                .bucket(bucketName)
                .build();

        client.makeBucket(makeBucketArgs);
    }

}

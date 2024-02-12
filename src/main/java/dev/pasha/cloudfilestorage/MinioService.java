package dev.pasha.cloudfilestorage;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinioService {

    public static void main(String[] args) throws Exception {
//        MinioClient client = MinioClient.builder()
//                .endpoint("http://localhost:9000")
//                .credentials("newUser", "$2a$10$paq3CTXy0bIkTJkABc0XW.c/PLzvBbSDWXJOIzJqp.7SPy9eXnxwK")
//                .build();
//        createBucket(client);
//        addBucketPolicy(client);
        Iterable<String> it1 = List.of("it1");
        Iterable<String> it2 = List.of("it2");
        Iterable<String> it3 = List.of("it3");
        List<Iterable<String>> input = List.of(it1, it2, it3);
        List<String> result = new ArrayList<>();
        input.forEach(iterable -> iterable.forEach(result::add));
        System.out.println(result);
    }

    private static void listObjects(MinioClient client) {
        String bucketName = "newuser--private-bucket";
        Iterable<Result<Item>> results = client.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build()
        );
        results.forEach(result -> {
            try {
                Item item = result.get();
                System.out.println("Item: " + "n" +
                                   "item.etag() -> " + item.etag() + "n" +
                                   "item.objectName() -> " + item.objectName() + "n" +
                                   "item.storageClass() -> " + item.storageClass() + "n" +
                                   "item.versionId() -> " + item.versionId() + "n" +
                                   "item.isDir() -> " + item.isDir() + "n"
                );
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static void uploadObject(MinioClient client) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, URISyntaxException {
        String bucketName = "newUser-private-bucket";
//        String objectName = "/temp/1.knew";
        String fileName = "C:\\Users\\pasha\\Desktop\\test.cpp";
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket(bucketName)
//                .object(objectName)
                .filename(fileName)
                .build();

        client.uploadObject(uploadObjectArgs);
    }

    private static void showBucketList(MinioClient client) throws Exception {
        client.listBuckets().forEach(b -> System.out.println("Bucket: " + b.name() + " " + b.creationDate()));

    }

    private static void createBucket(MinioClient client) throws Exception {
        String bucketName = "newuser-private-bucket";

        client.makeBucket(MakeBucketArgs.builder()
                .bucket(bucketName)
                .build());
    }

    private static void addBucketPolicy(MinioClient client) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String policyJson = "{ \"Version\": \"2012-10-17\", \"Statement\": [{ \"Sid\": \"newUser-write-only\", \"Effect\": \"Allow\", \"Principal\": {\"AWS\": [\"minio:users/newUser\"]}, \"Action\": [\"s3:PutObject\", \"s3:ListBucketMultipartUploads\", \"s3:AbortMultipartUpload\"], \"Resource\": [\"arn:aws:s3:::newuser-private-bucket/*\"] }] }";
//                    """
//                    {
//                        "Version": "2012-10-17",
//                        "Statement ": [
//                           {
//                              "Sid": "newUser-write-only",
//                              "Effect": "Allow",
//                              "Principal": "AWS": "arn:aws:iam::your-account-id:user/newUser",
//                              "Action": [
//                                 "s3:PutObject",
//                                 "s3:ListBucketMultipartUploads",
//                                 "s3:AbortMultipartUpload"
//                              ],
//                              "Resource": "arn:aws:s3::newuser-private-bucket/*"
//                          }
//                    }
//                """;

        client.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket("newuser-private-bucket")
                .config(policyJson)
                .build());
    }

}

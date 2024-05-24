package dev.pasha.cloudfilestorage.model;

import io.minio.messages.Item;

import java.util.UUID;

public class ItemWrapper {
    private Item item;
    private String name;
    private String url;
    private UUID id;
    private String urlWithoutName;

    public ItemWrapper(Item item) {
        this.item = item;
        this.name = extractName(item);
        this.url = deleteBucketName(item);
        this.id = UUID.randomUUID();
        this.urlWithoutName = deleteNameFromUrl();
    }

    private String deleteBucketName(Item item) {
        int indexOfSlash = item.objectName().indexOf("/");
        return item.objectName().substring(indexOfSlash + 1);
    }

    private String extractName(Item item) {
        String[] split = item.objectName().split("/");
        return split[split.length - 1];
    }

    private String deleteNameFromUrl() {
        if (url.contains("/")) {
            return url.substring(0, url.lastIndexOf("/"));
        } return "";
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlWithoutName() {
        return urlWithoutName;
    }
}

package dev.pasha.cloudfilestorage.model;

import io.minio.messages.Item;

public class ItemWrapper {
    private Item item;
    private String extractedName;
    private String url;

    public ItemWrapper(Item item) {
        this.item = item;
        this.extractedName = extractName(item);
        this.url = extractUrl(item);
    }

    private String extractUrl(Item item) {
        int indexOfSlash = item.objectName().indexOf("/");
        return item.objectName().substring(indexOfSlash + 1);
    }

    private String extractName(Item item) {
        String[] split = item.objectName().split("/");
        return split[split.length - 1];
    }

    public String getName() {
        return extractedName;
    }

    public Item getItem() {
        return item;
    }

    public String getUrl() {
        return url;
    }
}

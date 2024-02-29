package dev.pasha.cloudfilestorage.model;

import org.springframework.web.multipart.MultipartFile;

public class MinioObject {
    private String path;
    private MultipartFile file;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "MinioObject{" +
               ", path='" + path + '\'' +
               ", object=" + file.getName() +
               '}';
    }
}

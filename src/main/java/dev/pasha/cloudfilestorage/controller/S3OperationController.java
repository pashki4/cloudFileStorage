package dev.pasha.cloudfilestorage.controller;

import dev.pasha.cloudfilestorage.model.MinioObject;
import dev.pasha.cloudfilestorage.service.SimpleStorageService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class S3OperationController {
    private final SimpleStorageService simpleStorageService;

    @Autowired
    public S3OperationController(SimpleStorageService simpleStorageService) {
        this.simpleStorageService = simpleStorageService;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(value = "query") String query) {
        simpleStorageService.deleteObject(query);
        return "redirect:/";
    }

    @PostMapping("/rename")
    public String rename(@RequestParam("oldName") String oldName,
                         @RequestParam("newName") String newName) {
        simpleStorageService.renameObject(oldName, newName);
        simpleStorageService.deleteObject(oldName);
        return "redirect:/";
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public String upload(@ModelAttribute MinioObject minioObject) {
        simpleStorageService.putObject(minioObject);
        return "redirect:/";
    }
}

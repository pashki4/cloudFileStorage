package dev.pasha.cloudfilestorage.controller;

import dev.pasha.cloudfilestorage.model.ItemWrapper;
import dev.pasha.cloudfilestorage.service.SimpleStorageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        String path = extractPath(query);
        return "redirect:/?path=" + path;
    }

    @PostMapping("/rename")
    public String rename(@RequestParam("oldName") String oldName,
                         @RequestParam("newName") String newName) {
        simpleStorageService.renameObject(oldName, newName);
        simpleStorageService.deleteObject(oldName);
        String path = extractPath(oldName);
        return "redirect:/?path=" + path;
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public String upload(@RequestParam(value = "path", required = false) String path,
                         @RequestParam("file") MultipartFile multipartFile) {
        simpleStorageService.putObject(path, multipartFile);
        return "redirect:/?path=" + path;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "searchQuery", required = false) String searchQuery,
                         Model model) {
        List<ItemWrapper> searchResult = simpleStorageService.searchObjectByQuery(searchQuery);
        model.addAttribute("searchResult", searchResult);
        return "index";
    }

    @NotNull
    private static String extractPath(String query) {
        return query.substring(0, query.lastIndexOf("/"));
    }
}

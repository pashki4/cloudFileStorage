package dev.pasha.cloudfilestorage.controller;

import dev.pasha.cloudfilestorage.service.SimpleStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}

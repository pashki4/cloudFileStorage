package dev.pasha.cloudfilestorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NavigationController {

    @GetMapping("/folder")
    public String getRequestParam(@RequestParam(name = "path", required = false) String path) {
        System.out.println("Path: " + path);
        return "redirect:/";
    }

    @GetMapping("/folder/{id}")
    public String getPathVariable(@PathVariable(name = "id", required = false) Long id) {
        System.out.println("Id: " + id);
        return "redirect:/";
    }
}

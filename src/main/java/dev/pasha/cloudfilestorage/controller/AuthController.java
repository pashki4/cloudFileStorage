package dev.pasha.cloudfilestorage.controller;

import dev.pasha.cloudfilestorage.exception.UserAuthMinioServiceException;
import dev.pasha.cloudfilestorage.model.ItemWrapper;
import dev.pasha.cloudfilestorage.model.User;
import dev.pasha.cloudfilestorage.service.SimpleStorageService;
import dev.pasha.cloudfilestorage.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {
    private final UserRegistrationService userRegistrationService;
    private final SimpleStorageService simpleStorageService;

    @Autowired
    public AuthController(UserRegistrationService userRegistrationService,
                          SimpleStorageService simpleStorageService) {
        this.userRegistrationService = userRegistrationService;
        this.simpleStorageService = simpleStorageService;
    }

    @GetMapping("/")
    public String getIndex(@RequestParam(value = "path", required = false) String path,
                           Principal principal,
                           Model model) {

        if (principal != null) {
            List<ItemWrapper> items = simpleStorageService.getObjectsByPath(path);
            Map<String, String> breadCrumb = simpleStorageService.createBreadCrumb(path);
            model.addAttribute("items", items);
            model.addAttribute("breadCrumb", breadCrumb);
        }
        return "index";
    }

    @GetMapping("/login")
    public String getLoginForm() {
        return "login-form";
    }

    @GetMapping("/signup")
    public String getSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup-form";
    }

    @PostMapping("/signup")
    public String signup(User user) {
        try {
            userRegistrationService.register(user);
            simpleStorageService.createUser(user);
            return "redirect:/login";
        } catch (Exception e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new UserAuthMinioServiceException("Failure registering user", e);
        }
    }
}

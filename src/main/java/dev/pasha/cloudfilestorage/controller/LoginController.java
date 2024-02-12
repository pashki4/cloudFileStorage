package dev.pasha.cloudfilestorage.controller;

import dev.pasha.cloudfilestorage.model.User;
import dev.pasha.cloudfilestorage.service.SimpleStorageService;
import dev.pasha.cloudfilestorage.service.UserRegistrationService;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class LoginController {

    private final UserRegistrationService userRegistrationService;
    private final SimpleStorageService simpleStorageService;

    @Autowired
    public LoginController(UserRegistrationService userRegistrationService, SimpleStorageService simpleStorageService) {
        this.userRegistrationService = userRegistrationService;
        this.simpleStorageService = simpleStorageService;
    }

    @GetMapping("/")
    public String defaultPage() {
        return "unauth-header";
    }

    @GetMapping("/login")
    public String getLoginForm() {
        return "login-form";
    }

    @PostMapping("/auth")
    public String auth(Model model) {
        Iterable<Result<Item>> objects = simpleStorageService.getObjectsFromAllBuckets();
        model.addAttribute("objects", objects);
        return "auth-header";
    }

    @GetMapping("/signup")
    public String getSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup-form";
    }

    @PostMapping("/signup")
    public String signup(User user) {
        userRegistrationService.register(user);
        simpleStorageService.register(user);
        return "redirect:/auth";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView uniqueUsernameExceptionHandler(DataIntegrityViolationException ex) {
        ModelAndView model = new ModelAndView("signup-form");
        model.addObject("errorMessage", "This username is already in use");
        model.addObject("user", new User());
        return model;
    }
}

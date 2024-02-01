package dev.pasha.cloudfilestorage.controller;

import dev.pasha.cloudfilestorage.model.User;
import dev.pasha.cloudfilestorage.repository.UserRepository;
import dev.pasha.cloudfilestorage.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class LoginController {

    private final UserRegistrationService userRegistrationService;

    @Autowired
    public LoginController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @GetMapping("/")
    public String defaultPage() {
        return "unauth-header";
    }

    @GetMapping("/login")
    public String login() {
        return "login-form";
    }

    @GetMapping("/auth")
    public String auth() {
        return "auth-header";
    }

    @GetMapping("/signup")
    public String getSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup-form";
    }

    @PostMapping("/signup")
    public String signupUser(User user) {
        userRegistrationService.register(user);
        return "redirect:/";
    }
}

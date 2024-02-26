package dev.pasha.cloudfilestorage.controller;

import dev.pasha.cloudfilestorage.exception.UserAuthMinioServiceException;
import dev.pasha.cloudfilestorage.model.User;
import dev.pasha.cloudfilestorage.service.SimpleStorageService;
import dev.pasha.cloudfilestorage.service.UserRegistrationService;
import io.minio.Result;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
public class LoginController {

    private final UserRegistrationService userRegistrationService;
    private final SimpleStorageService simpleStorageService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(UserRegistrationService userRegistrationService, SimpleStorageService simpleStorageService, AuthenticationManager authenticationManager) {
        this.userRegistrationService = userRegistrationService;
        this.simpleStorageService = simpleStorageService;
        this.authenticationManager = authenticationManager;
    }

//    @PostMapping("/")
//    public String auth(Model model) {
//        Iterable<Result<Item>> objects = simpleStorageService.getObjectsByPath("");
//        model.addAttribute("objects", objects);
//        return "index";
//    }

    @GetMapping("/")
    public String getIndex(@RequestParam(value = "path", required = false) String path, Principal principal, Model model) throws Exception {
        if (principal != null) {
            Iterable<Result<Item>> objects = simpleStorageService.getObjectsByPath(path);
            BreadCrumbs breadCrumbs = createBreadCrumbs(objects);
            model.addAttribute("objects", objects);
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
    public String signup(HttpServletRequest request, User user) {
        String pass = user.getPassword();
        try {
            userRegistrationService.register(user);
            simpleStorageService.register(user);

            UsernamePasswordAuthenticationToken authToken
                    = new UsernamePasswordAuthenticationToken(user.getUsername(), pass);
            authToken.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
            return "redirect:/";
        } catch (Exception e) {
            throw new UserAuthMinioServiceException("Error registering user: " + user, e);
        }
    }
}

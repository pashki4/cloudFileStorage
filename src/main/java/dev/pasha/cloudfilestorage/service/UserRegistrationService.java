package dev.pasha.cloudfilestorage.service;


import dev.pasha.cloudfilestorage.model.User;
import dev.pasha.cloudfilestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        encodeUserPassword(user);
        userRepository.save(user);
    }

    private void encodeUserPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
}

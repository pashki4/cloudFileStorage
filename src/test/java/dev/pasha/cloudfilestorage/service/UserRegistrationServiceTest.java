package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.model.User;
import dev.pasha.cloudfilestorage.repository.UserRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRegistrationServiceTest {

    @Autowired
    UserRegistrationService userRegistrationService;
    @Autowired
    UserRepository userRepository;

    @Container
    @ServiceConnection
    static MySQLContainer<?> container = new MySQLContainer<>("mysql:8.2");

    @Test
    @Order(1)
    void userShouldBeRegistered() {
        User newUser = new User("user", "pass");
        userRegistrationService.register(newUser);
        Optional<User> registeredUser = userRepository.findByUsername(newUser.getUsername());
        assertThat(registeredUser.isPresent()).isTrue();
    }

    @Test
    @Order(2)
    void sameUsernameShouldThrowException() {
        User newUser = new User("user", "pass");
        assertThatThrownBy(() -> userRegistrationService.register(newUser));
    }

}
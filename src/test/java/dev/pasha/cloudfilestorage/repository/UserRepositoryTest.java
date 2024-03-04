package dev.pasha.cloudfilestorage.repository;

import dev.pasha.cloudfilestorage.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres");

    @Autowired
    UserRepository userRepository;

    @Test
    void connectionEstablished() {
        assertThat(container.isCreated()).isTrue();
        assertThat(container.isRunning()).isTrue();
    }

    @BeforeEach
    void setup() {
        List<User> users = List.of(new User("newUser", "password"),
                new User("newUser2", "password"));
        userRepository.saveAll(users);
    }

    @Test
    void shouldReturnUserByUserName() {
        Optional<User> user = userRepository.findByUsername("newUser");
        assertThat(user.isPresent()).isTrue();
    }

    @Test
    void shouldReturnCountOfUsers() {
        List<User> users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(2);
    }
}
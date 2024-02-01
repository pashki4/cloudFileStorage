package dev.pasha.cloudfilestorage.repository;

import dev.pasha.cloudfilestorage.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.2");

    @Autowired
    UserRepository userRepository;

    @Test
    void connectionEstablished() {
        assertThat(mySQLContainer.isCreated()).isTrue();
        assertThat(mySQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void setup() {
        List<User> users = List.of(new User("newUser", "password"));
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
        assertThat(users.size()).isEqualTo(1);
    }
}
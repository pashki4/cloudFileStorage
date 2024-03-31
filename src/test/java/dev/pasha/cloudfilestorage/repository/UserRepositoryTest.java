package dev.pasha.cloudfilestorage.repository;

import dev.pasha.cloudfilestorage.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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

    @Test
    void shouldReturnUserByUserName() {
        //Given
        User user = new User("newUser", "password");
        userRepository.save(user);

        //When
        Optional<User> actual = userRepository.findByUsername(user.getUsername());

        //Then
        assertThat(actual).isPresent();
    }

    @Test
    void shouldNotReturnUserWhenNameDoesNotExist() {
        //Given
        String name = "alex";

        //When
        Optional<User> actual = userRepository.findByUsername(name);

        //Then
        assertThat(actual).isNotPresent();
    }

    @Test
    void shouldReturnCountOfUsers() {
        //Given
        User user = new User("user", "password");
        userRepository.save(user);

        //When
        Optional<User> actual = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(user.getUsername()))
                .findAny();
        //Then
        assertThat(actual).isPresent().hasValue(user);
    }
}
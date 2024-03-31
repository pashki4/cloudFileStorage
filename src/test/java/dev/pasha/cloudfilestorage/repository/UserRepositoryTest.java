package dev.pasha.cloudfilestorage.repository;

import dev.pasha.cloudfilestorage.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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
    void shouldReturnUsers() {
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

    @Test
    void shouldReturnTrueWhenExistsUserByUsername() {
        //Given
        User user = new User("user", "password");
        userRepository.save(user);

        //When
        boolean actual = userRepository.existsUserByUsername(user.getUsername());

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnFalseWhenExistsUserByUsername() {
        //Given
        String username = "user";
        //When
        boolean actual = userRepository.existsUserByUsername(username);

        //Then
        assertThat(actual).isFalse();
    }
}
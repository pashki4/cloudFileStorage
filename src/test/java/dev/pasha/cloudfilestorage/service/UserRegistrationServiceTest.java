package dev.pasha.cloudfilestorage.service;

import dev.pasha.cloudfilestorage.exception.DuplicateUserException;
import dev.pasha.cloudfilestorage.model.User;
import dev.pasha.cloudfilestorage.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @InjectMocks
    private UserRegistrationService underTest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void userShouldBeRegistered() {
        //Given
        String password = "password";
        User user = new User("user", password);
        String encodedPassword = "encoded-password";

        //When
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);
        underTest.register(user);

        //Then
        InOrder inOrder = inOrder(userRepository, passwordEncoder);
        inOrder.verify(passwordEncoder).encode(password);
        inOrder.verify(userRepository).save(user);
    }

    @Test
    void sameUsernameShouldThrowException() {
        //Given
        User user = new User("user", "password");

        //When
        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(true);

        //Then
        assertThatThrownBy(() -> underTest.register(user)).isInstanceOf(DuplicateUserException.class)
                .hasMessage("username already taken");
    }

}
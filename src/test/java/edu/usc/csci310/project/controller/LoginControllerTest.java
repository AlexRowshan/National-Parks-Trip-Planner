package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.entity.UserEntity;
import edu.usc.csci310.project.service.JwtService;
import edu.usc.csci310.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = "apiKey=mockApiKey")
class LoginControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() {
        // Arrange
        String rawUsername = "username";
        String base64Username = Base64.getEncoder().encodeToString(rawUsername.getBytes());
        String rawPassword = "password";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        UserEntity userEntity = new UserEntity(base64Username, hashedPassword);
        when(userService.getUserByUsername(base64Username)).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = User.builder()
                .username(base64Username)
                .password(userEntity.getPassword())
                .roles("USER")
                .build();
        when(userService.loadUserByUsername(base64Username)).thenReturn(userDetails);

        String jwt = "mockJwtToken";
        when(jwtService.getJWT(userDetails)).thenReturn(jwt);

        // Act
        ResponseEntity<String> response = loginController.login(new UserEntity(rawUsername, rawPassword));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwt, response.getBody());
        verify(userService, times(1)).getUserByUsername(base64Username);
        verify(userService, times(1)).loadUserByUsername(base64Username);
        verify(jwtService, times(1)).getJWT(userDetails);
    }

    @Test
    public void testLogin_UserNotFound() {
        // Arrange
        UserEntity userEntity = new UserEntity("nonexistentuser", "password");
        when(userService.getUserByUsername(userEntity.getUsername())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = loginController.login(userEntity);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("UserEntity not found.", response.getBody());
        verify(userService, times(1)).getUserByUsername(userEntity.getUsername());
    }

    @Test
    public void testLogin_IncorrectPassword() {
        // Arrange
        String username = "username";
        String encodedUsername = Base64.getEncoder().encodeToString(username.getBytes());
        UserEntity userEntity = new UserEntity(username, "incorrectpassword");
        UserEntity foundUserEntity = new UserEntity(encodedUsername, "password");
        when(userService.getUserByUsername(encodedUsername)).thenReturn(Optional.of(foundUserEntity));

        // Act
        ResponseEntity<String> response = loginController.login(userEntity);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect password", response.getBody());
        verify(userService, times(1)).getUserByUsername(encodedUsername);
    }
}

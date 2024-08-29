package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.entity.UserEntity;
import edu.usc.csci310.project.service.JwtService;
import edu.usc.csci310.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@TestPropertySource(properties = "apiKey=mockApiKey")
class RegisterControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RegisterController registerController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        String rawUsername = "username";
        String base64Username = Base64.getEncoder().encodeToString(rawUsername.getBytes());
        String rawPassword = "password";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        UserEntity userEntity = new UserEntity(base64Username, hashedPassword);
        when(userService.createUser(any(UserEntity.class))).thenReturn(userEntity);

        UserDetails userDetails = User.builder()
                .username(base64Username)
                .password(userEntity.getPassword())
                .roles("USER")
                .build();
        when(userService.loadUserByUsername(base64Username)).thenReturn(userDetails);

        String jwt = "mockJwtToken";
        when(jwtService.getJWT(userDetails)).thenReturn(jwt);

        // Act
        ResponseEntity<String> response = registerController.registerUser(new UserEntity(rawUsername, rawPassword));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwt, response.getBody());
        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userService, times(1)).createUser(userEntityCaptor.capture());
        assertEquals(base64Username, userEntityCaptor.getValue().getUsername());
        verify(userService, times(1)).loadUserByUsername(base64Username);
        verify(jwtService, times(1)).getJWT(userDetails);
    }

    @Test
    public void testRegisterUser_UsernameTaken() {
        // Arrange
        UserEntity userEntity = new UserEntity("existinguser", "password");
        when(userService.createUser(userEntity)).thenReturn(null);

        // Act
        ResponseEntity<String> response = registerController.registerUser(userEntity);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username taken", response.getBody());
        verify(userService, times(1)).createUser(userEntity);
    }

    @Test
    public void testDeleteUser_Success() {
        // Arrange
        String username = "username";
        when(userService.deleteUser(username)).thenReturn(true);

        // Act
        ResponseEntity<String> response = registerController.deleteUser(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully delete user", response.getBody());
        verify(userService, times(1)).deleteUser(username);
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        // Arrange
        String username = "nonexistentuser";
        when(userService.deleteUser(username)).thenReturn(false);

        // Act
        ResponseEntity<String> response = registerController.deleteUser(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("UserEntity not found", response.getBody());
        verify(userService, times(1)).deleteUser(username);
    }
}
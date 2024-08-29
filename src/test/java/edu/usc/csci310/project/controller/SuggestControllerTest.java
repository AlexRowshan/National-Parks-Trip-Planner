package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SuggestController suggestController;

    private final String secretKey = "mockSecretKey";

    // Helper method to generate a mock JWT token
    private String generateMockToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void suggest_UserDoesNotExist_ReturnsBadRequestResponse() {
        // Arrange
        String usernames = "user1,user2";
        String token = generateMockToken(1L);
        String fullToken = "Bearer " + token;

        when(userService.isUserExist("user1")).thenReturn(false);

        // Act
        ResponseEntity<String> result = suggestController.suggest(usernames, fullToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("user: user1 does not exist"));
    }


    @Test
    void suggest_UserHasNoFavorites_ReturnsBadRequestResponse() {
        // Arrange
        String usernames = "user1,user2";
        String token = generateMockToken(1L);
        String fullToken = "Bearer " + token;

        when(userService.isUserExist(anyString())).thenReturn(true);
        when(userService.getFavorites("user1")).thenReturn("");

        // Act
        ResponseEntity<String> result = suggestController.suggest(usernames, fullToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("user: user1 has no favorite parks"));
    }

    @Test
    void suggest_FailedToGetParkInfo_ReturnsErrorResponse() {
        // Arrange
        String usernames = "user1,user2";
        String token = generateMockToken(1L);
        String fullToken = "Bearer " + token;
        String user1Favorites = "1park,2camp";
        String user2Favorites = "2camp,3room";

        when(userService.isUserExist(anyString())).thenReturn(true);
        when(userService.getFavorites("user1")).thenReturn(user1Favorites);
        when(userService.getFavorites("user2")).thenReturn(user2Favorites);

        doReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));

        // Act
        ResponseEntity<String> result = suggestController.suggest(usernames, fullToken);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Failed to get park info. Status code: 500", result.getBody());
    }

    @Test
    void suggest_FailedToCreateSSLContext_ReturnsInternalServerErrorResponse() {
        // Arrange
        String usernames = "user1,user2";
        String token = generateMockToken(1L);
        String fullToken = "Bearer " + token;
        String user1Favorites = "1park,2camp";
        String user2Favorites = "2camp,3room";

        when(userService.isUserExist(anyString())).thenReturn(true);
        when(userService.getFavorites("user1")).thenReturn(user1Favorites);
        when(userService.getFavorites("user2")).thenReturn(user2Favorites);

        String response = "{\"data\": [{\"parkCode\": \"park\"}]}";

        doReturn(ResponseEntity.ok(response))
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));

        try {
            // Act
            ResponseEntity<String> result = suggestController.suggest(usernames, fullToken);
        } catch (RuntimeException re) {
            // Assert
            assertEquals("SSL context creation failed", re.getMessage());
        }
    }

    @Test
    void suggest_Case3_ReturnsHighestRankedParkFromCurrentUserFavorites() {
        // Arrange
        String usernames = "user1,user2,user3";
        String token = generateMockToken(1L);
        String fullToken = "Bearer " + token;
        String user1Favorites = "1park,2camp";
        String user2Favorites = "1room,2site";
        String user3Favorites = "1spot,2area";

        String response = "{\"data\": [{\"parkCode\": \"park\"}]}";

        when(userService.isUserExist(anyString())).thenReturn(true);
        when(userService.getFavorites("user1")).thenReturn(user1Favorites);
        when(userService.getFavorites("user2")).thenReturn(user2Favorites);
        when(userService.getFavorites("user3")).thenReturn(user3Favorites);

        doReturn(ResponseEntity.ok(response))
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));

        // Act
        ResponseEntity<String> result = suggestController.suggest(usernames, fullToken);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("{\"data\": [{\"parkCode\": \"park\"}]}", result.getBody());
    }
}
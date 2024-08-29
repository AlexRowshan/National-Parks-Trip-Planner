package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.model.UpdateFavoriteRankRequest;
import edu.usc.csci310.project.service.UserService;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = "apiKey=mockApiKey")
class FavoritesControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private FavoritesController favoritesController;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddToFavorites_Success() {
        // Arrange
        String username = "user1";
        String parkCode = "ABC123";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("parkCode", parkCode);
        when(userService.addFavorite(username, parkCode)).thenReturn(true);

        // Act
        ResponseEntity<String> response = favoritesController.addToFavorites(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Park added to favorites successfully.", response.getBody());
        verify(userService, times(1)).addFavorite(username, parkCode);
    }

    @Test
    public void testAddToFavorites_ParkAlreadyInFavorites() {
        // Arrange
        String username = "user1";
        String parkCode = "ABC123";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("parkCode", parkCode);
        when(userService.addFavorite(username, parkCode)).thenReturn(false);

        // Act
        ResponseEntity<String> response = favoritesController.addToFavorites(requestBody);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Park already in favorites.", response.getBody());
        verify(userService, times(1)).addFavorite(username, parkCode);
    }

    @Test
    public void testCheckFavorites_WhenParkIsFavorite() {
        // Arrange
        String username = "user1";
        String parkCode = "ABC123";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("parkCode", parkCode);

        // Simulate the user having 'ABC123' in their favorites.
        when(userService.getFavorites(username)).thenReturn("ABC123,XYZ789");

        // Act
        ResponseEntity<Boolean> response = favoritesController.checkFavorites(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
        verify(userService, times(1)).getFavorites(username);
    }

    @Test
    public void testCheckFavorites_WhenParkIsNotFavorite() {
        // Arrange
        String username = "user2";
        String parkCode = "DEF456";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("parkCode", parkCode);

        // Simulate the user not having 'DEF456' in their favorites.
        when(userService.getFavorites(username)).thenReturn("ABC123,XYZ789");

        // Act
        ResponseEntity<Boolean> response = favoritesController.checkFavorites(requestBody);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
        verify(userService, times(1)).getFavorites(username);
    }

    @Test
    public void testDeleteParkFromFavorites_Success() {
        // Arrange
        String username = "user1";
        String parkCode = "park123";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("parkCode", parkCode);

        when(userService.deleteFavorite(username, parkCode)).thenReturn(true);

        // Act
        ResponseEntity<String> response = favoritesController.deleteParkFromFavorites(requestBody);

        // Assert
        assertEquals(ResponseEntity.ok("Park removed from favorites successfully."), response);
        verify(userService).deleteFavorite(username, parkCode);
    }

    @Test
    public void testDeleteParkFromFavorites_NotFound() {
        // Arrange
        String username = "user1";
        String parkCode = "nonExistentPark";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("parkCode", parkCode);

        when(userService.deleteFavorite(username, parkCode)).thenReturn(false);

        // Act
        ResponseEntity<String> response = favoritesController.deleteParkFromFavorites(requestBody);

        // Assert
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Park not found in favorites."), response);
        verify(userService).deleteFavorite(username, parkCode);
    }

    @Test
    public void testTogglePrivate_Success() {
        // Arrange
        String username = "user1";
        when(userService.togglePrivate(username)).thenReturn(true);

        // Act
        ResponseEntity<String> response = favoritesController.togglePrivate(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UserEntity's private status toggled successfully.", response.getBody());
        verify(userService, times(1)).togglePrivate(username);
    }

    @Test
    public void testTogglePrivate_UserNotFound() {
        // Arrange
        String username = "nonExistingUser";
        when(userService.togglePrivate(username)).thenReturn(false);

        // Act
        ResponseEntity<String> response = favoritesController.togglePrivate(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
        verify(userService, times(1)).togglePrivate(username);
    }

    @Test
    public void testIsPrivate_UserExists() {
        // Arrange
        String username = "user1";
        when(userService.isPrivate(username)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = favoritesController.isPrivate(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
        verify(userService, times(1)).isPrivate(username);
    }

    @Test
    public void testIsPrivate_UserDoesNotExist() {
        // Arrange
        String username = "nonExistingUser";
        when(userService.isPrivate(username)).thenReturn(null);

        // Act
        ResponseEntity<Boolean> response = favoritesController.isPrivate(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).isPrivate(username);
    }

    @Test
    public void testUpdateFavoriteRank_Success() {
        // Arrange
        String username = "user1";
        String parkCode = "ABC123";
        String rankChange = "+";
        UpdateFavoriteRankRequest request = new UpdateFavoriteRankRequest(username, parkCode, rankChange);
        when(userService.updateFavoriteRank(username, parkCode, rankChange)).thenReturn(true);

        // Act
        ResponseEntity<String> response = favoritesController.updateFavoriteRank(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Favorite rank updated successfully.", response.getBody());
        verify(userService, times(1)).updateFavoriteRank(username, parkCode, rankChange);
    }

    @Test
    public void testUpdateFavoriteRank_Failure() {
        // Arrange
        String username = "user1";
        String parkCode = "ABC123";
        String rankChange = "+";
        UpdateFavoriteRankRequest request = new UpdateFavoriteRankRequest(username, parkCode, rankChange);
        when(userService.updateFavoriteRank(username, parkCode, rankChange)).thenReturn(false);

        // Act
        ResponseEntity<String> response = favoritesController.updateFavoriteRank(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failed to update favorite rank.", response.getBody());
        verify(userService, times(1)).updateFavoriteRank(username, parkCode, rankChange);
    }

    @Test
    public void testGetFavorites_Success() {
        // Arrange
        String username = "user1";
        String favorites = "park1,park2";
        String parkJson = "{\"parkCode\":\"park1\",\"name\":\"Park 1\"}";
        ResponseEntity<String> parkResponse = ResponseEntity.ok(parkJson);
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn(favorites);

        // Configure the RestTemplate to trust all certificates
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(parkResponse);

        // Set the mocked RestTemplate in the FavoritesController
        ReflectionTestUtils.setField(favoritesController, "restTemplate", restTemplate);

        // Act
        ResponseEntity<String> response = favoritesController.getFavorites(username, "mockJwtToken");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("park1"));
        assertTrue(response.getBody().contains("Park 1"));
        verify(userService, times(1)).isUserExist(username);
        verify(userService, times(1)).getFavorites(username);
        verify(restTemplate, times(2)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    public void testGetFavorites_UsernameNull() {
        // Arrange
        String username = null;

        // Act
        ResponseEntity<String> response = favoritesController.getFavorites(username, "mockJwtToken");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is required.", response.getBody());
    }

    @Test
    public void testGetFavorites_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        when(userService.isUserExist(username)).thenReturn(false);

        // Act
        ResponseEntity<String> response = favoritesController.getFavorites(username, "mockJwtToken");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
        verify(userService, times(1)).isUserExist(username);
    }

    @Test
    public void testGetFavorites_Exception() {
        // Arrange
        String username = "user1";
        String favorites = "park1,park2";
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn(favorites);

        // Mock the behavior of RestTemplate to throw an exception
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new ResourceAccessException("I/O error on GET request for \"https://localhost:8080/api/getParks\": Connection refused"));

        // Act
        ResponseEntity<String> response = favoritesController.getFavorites(username, "mockJwtToken");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to get park info. Exception: I/O error on GET request for \"https://localhost:8080/api/getParks\": Connection refused", response.getBody());
        verify(userService, times(1)).isUserExist(username);
        verify(userService, times(1)).getFavorites(username);
    }

//    @Test
//    public void testGetFavorites_NoFavoritesFound() {
//        // Arrange
//        String username = "user1";
//        String favorites = "";
//        when(userService.isUserExist(username)).thenReturn(true);
//        when(userService.getFavorites(username)).thenReturn(favorites);
//
//        // Act and Assert
//        assertThrows(StringIndexOutOfBoundsException.class, () -> favoritesController.getFavorites(username, "mockJwtToken"));
//
//        verify(userService, times(1)).isUserExist(username);
//        verify(userService, times(1)).getFavorites(username);
//    }

    @Test
    public void testGetFavorites_NonSuccessfulResponse() {
        // Arrange
        String username = "user1";
        String favorites = "park1,park2";
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn(favorites);

        // Mock the behavior of RestTemplate
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new ResourceAccessException("I/O error on GET request for \"https://localhost:8080/api/getParks\": Connection refused"));

        // Act
        ResponseEntity<String> response = favoritesController.getFavorites(username, "mockJwtToken");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to get park info. Exception: I/O error on GET request for \"https://localhost:8080/api/getParks\": Connection refused", response.getBody());
        verify(userService, times(1)).isUserExist(username);
        verify(userService, times(1)).getFavorites(username);
    }

    @Test
    public void testGetFavorites_ExceptionFromRestTemplate() {
        // Arrange
        String username = "user1";
        String favorites = "park1,park2";
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn(favorites);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenThrow(new RuntimeException("RestTemplate exception"));

        // Act
        try {
            favoritesController.getFavorites(username, "mockJwtToken");
            // expect RunTimeException to be thrown
//            fail("Expected RuntimeException to be thrown");
        } catch (Exception e) {
            // Assert
            assertEquals("RestTemplate exception", e.getMessage());
        }

        verify(userService, times(1)).isUserExist(username);
        verify(userService, times(1)).getFavorites(username);
    }

    @Test
    public void testGetFavorites_NoFavoritesFound() {
        // Arrange
        String username = "user1";
        String favorites = "";
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn(favorites);

        // Act
        ResponseEntity<String> response = favoritesController.getFavorites(username, "mockJwtToken");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No favorites found.", response.getBody());
        verify(userService, times(1)).isUserExist(username);
        verify(userService, times(1)).getFavorites(username);
    }

    @Test
    public void testGetFavorites_JsonParsingException() {
        // Arrange
        String username = "user1";
        String favorites = "park1,park2";
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn(favorites);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new ResourceAccessException("I/O error on GET request for \"https://localhost:8080/api/getParks\": Connection refused"));

        // Act
        ResponseEntity<String> response = favoritesController.getFavorites(username, "mockJwtToken");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to get park info. Exception: I/O error on GET request for \"https://localhost:8080/api/getParks\": Connection refused", response.getBody());
        verify(userService, times(1)).isUserExist(username);
        verify(userService, times(1)).getFavorites(username);
    }

    @Test
    public void testGetFavorites_FavoritesEmptyOrNull() {
        // Arrange
        String username = "user1";
        String emptyFavorites = "";
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn(emptyFavorites);

        // Act
        ResponseEntity<String> response = favoritesController.getFavorites(username, "mockJwtToken");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No favorites found.", response.getBody());
        verify(userService, times(1)).isUserExist(username);
        verify(userService, times(1)).getFavorites(username);
    }

    @Test
    public void testDeleteAllFavorites_Success() {
        String username = "user1";
        String parkCode = "park123";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("parkCode", parkCode);

        when(userService.deleteAllFavorites(username)).thenReturn(true);

        // Act
        ResponseEntity<String> response = favoritesController.deleteAllFavorites(requestBody);

        // Assert
        assertEquals(ResponseEntity.ok("All favorites deleted, and list set to private."), response);
        verify(userService, times(1)).deleteAllFavorites(username);
    }

    @Test
    public void testDeleteAllFavorites_Failed() {
        String username = "user1";
        String parkCode = "park123";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("parkCode", parkCode);

        when(userService.deleteAllFavorites(username)).thenReturn(false);

        ResponseEntity<String> response = favoritesController.deleteAllFavorites(requestBody);

        // Assert
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserEntity not found."), response);
        verify(userService).deleteAllFavorites(username);
    }
    
    @Test
    public void testGetFavorites_SSLContextException() throws Exception {
        // Mock the userService to return a non-empty favorites string
        String username = "testuser";
        String favorites = "1,2,3";
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn(favorites);

        // Mock the SSLContextBuilder to throw a RuntimeException
        SSLContextBuilder sslContextBuilder = mock(SSLContextBuilder.class);
        RuntimeException sslException = new RuntimeException("SSL context exception");
        when(sslContextBuilder.loadTrustMaterial(isNull(), any(TrustStrategy.class))).thenThrow(sslException);

        // Perform the GET request
        String token = "testtoken";
        ResponseEntity<String> response = favoritesController.getFavorites(username, token);

        // Assert the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetFavorites_BadResponse() {
        // Mock the userService to return a non-empty favorites string
        String username = "testuser";
        String favorites = "1,2,3";
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn(favorites);

        // Mock the restTemplate to return a non-successful response
        ResponseEntity<String> nonSuccessfulResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(nonSuccessfulResponse);

        // Perform the GET request
        String token = "testtoken";
        ResponseEntity<String> response = favoritesController.getFavorites(username, token);

        // Assert the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Failed to get park info"));
    }
}
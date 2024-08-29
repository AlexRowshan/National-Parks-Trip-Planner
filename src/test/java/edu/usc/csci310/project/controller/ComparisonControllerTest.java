package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ComparisonControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ComparisonController comparisonController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void compare_SingleUser_Success() {
//        String username = "u";
//        String encodedUsername = Base64.getEncoder().encodeToString(username.getBytes());
//        when(userService.isUserExist(encodedUsername)).thenReturn(true);
//        when(userService.getFavorites(encodedUsername)).thenReturn("1yose");
//
//        String baseUrl = "https://localhost:8080";
//        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/api/getParks").queryParam("parkCode", "yose");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer mockJwtToken");
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        when(restTemplate.exchange(
//                uriBuilder.toUriString(),
//                HttpMethod.GET,
//                entity,
//                String.class)).thenReturn(ResponseEntity.ok("{\"data\": \"park data\"}"));
//
//        ResponseEntity<Map<String, Object>> response = comparisonController.compare(encodedUsername, "Bearer mockJwtToken");
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        Map<String, Object> responseBody = response.getBody();
//        assertNotNull(responseBody);
//        assertEquals(encodedUsername, responseBody.get("users"));
//        assertEquals(1, responseBody.get("total"));
//        List<Map<String, Object>> parkDataList = (List<Map<String, Object>>) responseBody.get("parkData");
//        assertEquals(1, parkDataList.size());
//        Map<String, Object> parkData = parkDataList.get(0);
//        assertEquals("yose", parkData.get("parkCode"));
//        assertEquals(1, parkData.get("frequency"));
//        assertEquals("{\"data\": \"park data\"}", parkData.get("data"));
//    }

//    @Test
//    void compare_MultipleUsers_Success() {
//        String username1 = "u1";
//        String username2 = "u2";
//        when(userService.isUserExist(username1)).thenReturn(true);
//        when(userService.isUserExist(username2)).thenReturn(true);
//        when(userService.getFavorites(username1)).thenReturn("1yose");
//        when(userService.getFavorites(username2)).thenReturn("1jomu,2yose");
//
//        String baseUrl = "https://localhost:8080";
//        UriComponentsBuilder uriBuilder1 = UriComponentsBuilder.fromHttpUrl(baseUrl)
//                .path("/api/getParks")
//                .queryParam("parkCode", "yose");
//        UriComponentsBuilder uriBuilder2 = UriComponentsBuilder.fromHttpUrl(baseUrl)
//                .path("/api/getParks")
//                .queryParam("parkCode", "jomu");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer mockJwtToken");
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        when(restTemplate.exchange(
//                uriBuilder1.toUriString(),
//                HttpMethod.GET,
//                entity,
//                String.class
//        )).thenReturn(ResponseEntity.ok("{\"data\": \"yose data\"}"));
//        when(restTemplate.exchange(
//                uriBuilder2.toUriString(),
//                HttpMethod.GET,
//                entity,
//                String.class
//        )).thenReturn(ResponseEntity.ok("{\"data\": \"jomu data\"}"));
//
//        ResponseEntity<Map<String, Object>> response = comparisonController.compare(username1 + "," + username2, "Bearer mockJwtToken");
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        Map<String, Object> responseBody = response.getBody();
//        assertNotNull(responseBody);
//        assertEquals("u1,u2", responseBody.get("users"));
//        assertEquals(2, responseBody.get("total"));
//        List<Map<String, Object>> parkDataList = (List<Map<String, Object>>) responseBody.get("parkData");
//        assertEquals(2, parkDataList.size());
//        Map<String, Object> parkData1 = parkDataList.get(0);
//        assertEquals("yose", parkData1.get("parkCode"));
//        assertEquals(2, parkData1.get("frequency"));
//        assertEquals("{\"data\": \"yose data\"}", parkData1.get("data"));
//        Map<String, Object> parkData2 = parkDataList.get(1);
//        assertEquals("jomu", parkData2.get("parkCode"));
//        assertEquals(1, parkData2.get("frequency"));
//        assertEquals("{\"data\": \"jomu data\"}", parkData2.get("data"));
//    }


//    @Test
//    void compare_ParkDataNotSuccessful() {
//        String username1 = "u1";
//        String username2 = "u2";
//        when(userService.isUserExist(username1)).thenReturn(true);
//        when(userService.isUserExist(username2)).thenReturn(true);
//        when(userService.getFavorites(username1)).thenReturn("1yose");
//        when(userService.getFavorites(username2)).thenReturn("1jomu,2yose");
//
//        String baseUrl = "https://localhost:8080";
//        UriComponentsBuilder uriBuilder1 = UriComponentsBuilder.fromHttpUrl(baseUrl)
//                .path("/api/getParks")
//                .queryParam("parkCode", "yose");
//        UriComponentsBuilder uriBuilder2 = UriComponentsBuilder.fromHttpUrl(baseUrl)
//                .path("/api/getParks")
//                .queryParam("parkCode", "jomu");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer mockJwtToken");
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        when(restTemplate.exchange(
//                uriBuilder1.toUriString(),
//                HttpMethod.GET,
//                entity,
//                String.class)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
//        when(restTemplate.exchange(uriBuilder2.toUriString(),
//                HttpMethod.GET,
//                entity,
//                String.class
//        )).thenReturn(ResponseEntity.ok("{\"data\": \"jomu data\"}"));
//
//        ResponseEntity<Map<String, Object>> response = comparisonController.compare(username1 + "," + username2, "Bearer mockJwtToken");
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        Map<String, Object> responseBody = response.getBody();
//        assertNotNull(responseBody);
//        assertEquals("u1,u2", responseBody.get("users"));
//        assertEquals(2, responseBody.get("total"));
//        List<Map<String, Object>> parkDataList = (List<Map<String, Object>>) responseBody.get("parkData");
//        assertEquals(1, parkDataList.size());
//        Map<String, Object> parkData = parkDataList.get(0);
//        assertEquals("jomu", parkData.get("parkCode"));
//        assertEquals(1, parkData.get("frequency"));
//        assertEquals("{\"data\": \"jomu data\"}", parkData.get("data"));
//    }

    @Test
    void compare_UserDoesNotExist() {
        String username = "u";
        when(userService.isUserExist(username)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = comparisonController.compare(username, "mockJwtToken");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("ERROR: user: " + username + " does not exist", responseBody.get("error"));
    }

    @Test
    void compare_UserHasNoFavoriteParks() {
        String username = "u";
        when(userService.isUserExist(username)).thenReturn(true);
        when(userService.getFavorites(username)).thenReturn("");

        ResponseEntity<Map<String, Object>> response = comparisonController.compare(username, "mockJwtToken");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("ERROR: user: " + username + " has no favorite parks", responseBody.get("error"));
    }
}
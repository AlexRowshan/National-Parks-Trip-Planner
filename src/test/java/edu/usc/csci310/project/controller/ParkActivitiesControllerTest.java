package edu.usc.csci310.project.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ParkActivitiesControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ParkActivitiesController parkActivitiesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getParkActivities_NullParkCode_ReturnsOkResponse() {
        String expectedResponse = "Activities data";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkActivitiesController.getParkActivities(null, null,null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getParkActivities_EmptyParkCode_ReturnsOkResponse() {
        String expectedResponse = "Activities data";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkActivitiesController.getParkActivities("", "",null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getParkActivities_ValidParkCode_ReturnsOkResponse() {
        String parkCode = "ABCD";
        String expectedResponse = "Activities data for ABCD";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkActivitiesController.getParkActivities(parkCode, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getParkActivities_ShortParkCode_ReturnsBadRequestResponse() {
        String parkCode = "ABC";
        String expectedErrorMessage = "Park code must be between 4 and 10 characters";

        ResponseEntity<String> response = parkActivitiesController.getParkActivities(parkCode, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody());
    }

    @Test
    void getParkActivities_LongParkCode_ReturnsBadRequestResponse() {
        String parkCode = "ABCDEFGHIJK";
        String expectedErrorMessage = "Park code must be between 4 and 10 characters";

        ResponseEntity<String> response = parkActivitiesController.getParkActivities(parkCode, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody());
    }

    @Test
    void getParkActivities_RestTemplateException_ReturnsInternalServerErrorResponse() {
        String parkCode = "ABCD";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("RestTemplate exception"));

        ResponseEntity<String> response = parkActivitiesController.getParkActivities(parkCode, null, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to retrieve activities data", response.getBody());
    }

    @Test
    void getParkAmenities_ValidQterm_ReturnsOkResponse() {
        String q = "surfing";
        String expectedResponse = "Amenities data for surfing";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkActivitiesController.getParkActivities(null, "surfing", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}
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

class ParkAmenitiesControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ParkAmenitiesController parkAmenitiesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getParkAmenities_NullParkCode_ReturnsOkResponse() {
        String expectedResponse = "Amenities data";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkAmenitiesController.getParkAmenities(null, null,null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getParkAmenities_EmptyParkCode_ReturnsOkResponse() {
        String expectedResponse = "Amenities data";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkAmenitiesController.getParkAmenities("", "",null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getParkAmenities_ValidParkCode_ReturnsOkResponse() {
        String parkCode = "ABCD";
        String expectedResponse = "Amenities data for ABCD";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkAmenitiesController.getParkAmenities(parkCode, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getParkAmenities_ShortParkCode_ReturnsBadRequestResponse() {
        String parkCode = "ABC";
        String expectedErrorMessage = "Park code must be between 4 and 10 characters";

        ResponseEntity<String> response = parkAmenitiesController.getParkAmenities(parkCode, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody());
    }

    @Test
    void getParkAmenities_LongParkCode_ReturnsBadRequestResponse() {
        String parkCode = "ABCDEFGHIJK";
        String expectedErrorMessage = "Park code must be between 4 and 10 characters";

        ResponseEntity<String> response = parkAmenitiesController.getParkAmenities(parkCode, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody());
    }

    @Test
    void getParkAmenities_RestTemplateException_ReturnsInternalServerErrorResponse() {
        String parkCode = "ABCD";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("RestTemplate exception"));

        ResponseEntity<String> response = parkAmenitiesController.getParkAmenities(parkCode, null, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to retrieve amenities data", response.getBody());
    }

    @Test
    void getParkAmenities_ValidQterm_ReturnsOkResponse() {
        String q = "Wheelchair Accessible";
        String expectedResponse = "Amenities data for Wheelchair Accessible";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkAmenitiesController.getParkAmenities(null, "Wheelchair Accessible", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}
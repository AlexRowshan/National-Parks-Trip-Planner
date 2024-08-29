package edu.usc.csci310.project.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = "apiKey=mockApiKey")
public class ParkControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ParkController parkController;

    @Value("${apiKey}")
    private String apiKey;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(parkController, "apiKey", apiKey);
    }

    @Test
    public void testGetParks_ValidRequest() {
        // Arrange
        String parksResponse = "{\"total\":1,\"limit\":10,\"start\":0,\"data\":[{\"parkCode\":\"acad\"}]}";
        String amenitiesResponse = "{\"data\":[[{\"name\":\"Amenity 1\"},{\"name\":\"Amenity 2\"}]]}";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(parksResponse))
                .thenReturn(ResponseEntity.ok(amenitiesResponse));

        // Act
        ResponseEntity<String> response = parkController.getParks("Acadia", null, 10, 0, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, jsonNode.get("total").asInt());
        assertEquals(10, jsonNode.get("limit").asInt());
        assertEquals(0, jsonNode.get("start").asInt());
        assertEquals("acad", jsonNode.get("parks").get(0).get("parkCode").asText());
        assertEquals("Amenity 1", jsonNode.get("amenities").get("acad").get(0).asText());
        assertEquals("Amenity 2", jsonNode.get("amenities").get("acad").get(1).asText());
    }
    @Test
    public void testGetParks_InvalidParkCode() {
        // Arrange
        String invalidParkCode = "abc";

        // Act
        ResponseEntity<String> response = parkController.getParks(null, invalidParkCode, null, null, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Park code must be between 4 and 10 characters", response.getBody());
    }

    @Test
    public void testGetParks_Exception() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Failed to retrieve park data"));

        // Act
        ResponseEntity<String> response = parkController.getParks(null, null, null, null, null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to retrieve park data", response.getBody());
    }

    @Test
    public void testGetParks_EmptyParkCode() {
        // Arrange
        String parkCode = "";

        // Act
        ResponseEntity<String> response = parkController.getParks("Yosemite", parkCode, null, null, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Park code must be between 4 and 10 characters", response.getBody());
    }

    @Test
    public void testGetParks_LongParkCode() {
        // Arrange
        String longParkCode = "abcdefghijklmnop"; // >10 characters

        // Act
        ResponseEntity<String> response = parkController.getParks(null, longParkCode, null, null, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Park code must be between 4 and 10 characters", response.getBody());
    }

    @Test
    public void testGetParks_CustomLimit() {
        // Arrange
        int customLimit = 20; // Custom limit value
        String expectedResponse = generateExpectedResponseWithLimit(customLimit);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        ResponseEntity<String> response = parkController.getParks(null, null, customLimit, 0, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("\"limit\":\"" + customLimit + "\""));
    }

    @Test
    public void testGetParks_ParkCode() {
        // Arrange
        String parkCode = "yose";
        String expectedResponse = generateExpectedResponseWithLimit(1);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        ResponseEntity<String> response = parkController.getParks(null, parkCode, null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetParks_ChainingParkCodesGreaterThan10andNoCommaAtChar4() {
        // Arrange
        String parkCode = "gkhfjghfghfhjgfjgfjfiuytyy!";

        // Act
        ResponseEntity<String> response = parkController.getParks(null, parkCode, null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetParks_ChainingParkCodesGreaterThan10andSpaceAtChar3() {
        // Arrange
        String parkCode = "yos ,mite, alca, albi";

        // Act
        ResponseEntity<String> response = parkController.getParks(null, parkCode, null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void testGetParks_ChainingParkCodesGreaterThan10() {
        // Arrange
        String parkCode = "yose,alca,albi";

        String expectedResponse = generateExpectedResponseWithLimit(1);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        ResponseEntity<String> response = parkController.getParks(null, parkCode, null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetParks_ByName() {
        String query = "Yosemite";
        String expectedResponse = generateExpectedResponseWithLimit(1);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkController.getParks(query, null, null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetParks_ByState() {
        String query = "CA";
        String expectedResponse = generateExpectedResponseWithLimit(1);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<String> response = parkController.getParks(null, null, null, null, query);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetParks_AmenitiesLoop() {
        // Arrange
        String parkCode = "yose";
        String expectedParksResponse = "{\"total\":1,\"limit\":10,\"start\":0,\"data\":[{\"parkCode\":\"yose\"}]}";
        String expectedAmenitiesResponse = "{\"data\":[[{\"name\":\"Amenity1\"},{\"name\":\"Amenity2\"}],[{\"name\":\"Amenity3\"}]]}";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedParksResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(expectedAmenitiesResponse, HttpStatus.OK));

        // Act
        ResponseEntity<String> response = parkController.getParks(null, parkCode, null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode amenitiesNode = responseJson.path("amenities");
        assertTrue(amenitiesNode.isObject());

        JsonNode parkAmenitiesNode = amenitiesNode.path(parkCode);
        assertTrue(parkAmenitiesNode.isArray());

        List<String> expectedAmenities = Arrays.asList("Amenity1", "Amenity2", "Amenity3");
        List<String> actualAmenities = new ArrayList<>();
        for (JsonNode amenityNode : parkAmenitiesNode) {
            actualAmenities.add(amenityNode.asText());
        }
        assertEquals(expectedAmenities, actualAmenities);
    }

    @Test
    public void testGetParks_EmptyQuery() {
        // Arrange
        String expectedParksResponse = "{\"total\":10,\"limit\":10,\"start\":0,\"data\":[{\"parkCode\":\"park1\"},{\"parkCode\":\"park2\"}]}";
        String expectedAmenitiesResponse = "{\"data\":[[{\"name\":\"Amenity1\"},{\"name\":\"Amenity2\"}],[{\"name\":\"Amenity3\"}]]}";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedParksResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(expectedAmenitiesResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(expectedAmenitiesResponse, HttpStatus.OK));

        // Act
        ResponseEntity<String> response = parkController.getParks("", null, null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode parksNode = responseJson.path("parks");
        assertTrue(parksNode.isArray());
        assertEquals(2, parksNode.size());
    }

    @Test
    public void testGetParks_NullStateCode() {
        // Arrange
        String expectedParksResponse = "{\"total\":10,\"limit\":10,\"start\":0,\"data\":[{\"parkCode\":\"park1\"},{\"parkCode\":\"park2\"}]}";
        String expectedAmenitiesResponse = "{\"data\":[[{\"name\":\"Amenity1\"},{\"name\":\"Amenity2\"}],[{\"name\":\"Amenity3\"}]]}";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedParksResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(expectedAmenitiesResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(expectedAmenitiesResponse, HttpStatus.OK));

        // Act
        ResponseEntity<String> response = parkController.getParks(null, null, null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode parksNode = responseJson.path("parks");
        assertTrue(parksNode.isArray());
        assertEquals(2, parksNode.size());
    }

    @Test
    public void testGetParks_EmptyStateCode() {
        // Arrange
        String expectedParksResponse = "{\"total\":10,\"limit\":10,\"start\":0,\"data\":[{\"parkCode\":\"park1\"},{\"parkCode\":\"park2\"}]}";
        String expectedAmenitiesResponse = "{\"data\":[[{\"name\":\"Amenity1\"},{\"name\":\"Amenity2\"}],[{\"name\":\"Amenity3\"}]]}";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedParksResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(expectedAmenitiesResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(expectedAmenitiesResponse, HttpStatus.OK));

        // Act
        ResponseEntity<String> response = parkController.getParks(null, null, null, null, "");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode parksNode = responseJson.path("parks");
        assertTrue(parksNode.isArray());
        assertEquals(2, parksNode.size());
    }

    @Test
    public void testGetParks_NonEmptyStateCode() {
        // Arrange
        String stateCode = "CA";
        String expectedParksResponse = "{\"total\":2,\"limit\":10,\"start\":0,\"data\":[{\"parkCode\":\"park1\"},{\"parkCode\":\"park2\"}]}";
        String expectedAmenitiesResponse = "{\"data\":[[{\"name\":\"Amenity1\"},{\"name\":\"Amenity2\"}],[{\"name\":\"Amenity3\"}]]}";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedParksResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(expectedAmenitiesResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(expectedAmenitiesResponse, HttpStatus.OK));

        // Act
        ResponseEntity<String> response = parkController.getParks(null, null, null, null, stateCode);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode parksNode = responseJson.path("parks");
        assertTrue(parksNode.isArray());
        assertEquals(2, parksNode.size());
    }

    private String generateExpectedResponseWithLimit(int customLimit) {
        return "{" +
                "\"total\":\"1\"," +
                "\"limit\":\"" + customLimit + "\"," +
                "\"parks\": [{" +
                "\"id\": \"4324B2B4-D1A3-497F-8E6B-27171FAE4DB2\"," +
                "\"url\": \"https://www.nps.gov/yose/index.htm\"," +
                "\"fullName\": \"Yosemite National Park\"," +
                "\"parkCode\": \"" + "yose" + "\"," +
                "\"description\": \"Not just a great valley, but a shrine to human foresight, the strength of granite, the power of glaciers, the persistence of life, and the tranquility of the High Sierra. First protected in 1864, Yosemite National Park is best known for its waterfalls, but within its nearly 1,200 square miles, you can find deep valleys, grand meadows, ancient giant sequoias, a vast wilderness area, and much more.\"," +
                "\"latitude\": \"37.84883288\"," +
                "\"longitude\": \"-119.5571873\"," +
                "\"latLong\": \"lat:37.84883288, long:-119.5571873\"," +
                "\"activities\": [{" +
                "\"id\": \"09DF0950-D319-4557-A57E-04CD2F63FF42\"," +
                "\"name\": \"Arts and Culture\"" +
                "}]," +
                "\"topics\": [{" +
                "\"id\": \"69693007-2DF2-4EDE-BB3B-A25EBA72BDF5\"," +
                "\"name\": \"Architecture and Building\"" +
                "}]," +
                "\"states\": \"CA\"," +
                "\"contacts\": {" +
                "\"phoneNumbers\": [{" +
                "\"phoneNumber\": \"209/372-0200\"," +
                "\"description\": \"\"," +
                "\"extension\": \"\"," +
                "\"type\": \"Voice\"" +
                "}]," +
                "\"emailAddresses\": [{" +
                "\"description\": \"\"," +
                "\"emailAddress\": \"yose_web_manager@nps.gov\"" +
                "}]" +
                "}" +
                "}]," +
                "\"amenities\": {" +
                "\"" + "yose" + "\": [\"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\"]" +
                "}" +
                "}";
    }
}
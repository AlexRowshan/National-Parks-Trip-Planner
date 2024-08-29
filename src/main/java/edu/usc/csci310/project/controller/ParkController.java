package edu.usc.csci310.project.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class ParkController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${apiKey}")
    private String apiKey;

    @GetMapping("/api/getParks")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> getParks(@RequestParam(required = false) String q, @RequestParam(required = false) String parkCode, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer start, @RequestParam(required = false) String stateCode) {

        String baseUrl = "https://developer.nps.gov/api/v1/";

        UriComponentsBuilder parksUriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + "parks");
        parksUriBuilder.queryParam("api_key", apiKey).queryParam("sort", "-relevanceScore").queryParam("limit", limit).queryParam("start", start);

        //Check if park code is provided and meets length requirements
        if (parkCode != null)
        {
            if(parkCode.length() < 4)
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Park code must be between 4 and 10 characters");
            }
            else if(parkCode.length() > 10 && parkCode.charAt(3) == ' ') //Can only be greater than 10 if chaining park codes together
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Park code must be between 4 and 10 characters");
            }
            else if(parkCode.length() > 10 && parkCode.charAt(4) != ',')
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Park code must be between 4 and 10 characters");
            }
            else
            {
                parksUriBuilder.queryParam("parkCode", parkCode);
            }
        }

        if (q != null && !q.isEmpty()) {
            parksUriBuilder.queryParam("q", q);
        }
        if (stateCode != null && !stateCode.isEmpty()) {
            parksUriBuilder.queryParam("stateCode", stateCode);
        }
        String parksUri = parksUriBuilder.toUriString(); //Making parks call uri

        //Get list of park codes and call the amenities/parkplaces endpoint on each park code
        try {
            ResponseEntity<String> parkResponse = restTemplate.getForEntity(parksUri, String.class);
            //return ResponseEntity.ok(parkResponse.getBody());
            ObjectMapper ObjectMapper = new ObjectMapper();
            JsonNode parksJson = ObjectMapper.readTree(parkResponse.getBody()); //List of parks in JSON

            Map<String, List<String>> parkAmenitiesMap = new HashMap<>();

            for (JsonNode park : parksJson.path("data")) //Loop through each park in the list of parks and...
            {
                String parkCodeValue = park.path("parkCode").asText(); //Get the park code.

                //Now call the amenities endpoint for current park code...
                UriComponentsBuilder amenitiesURI = UriComponentsBuilder.fromHttpUrl(baseUrl + "/amenities/parksplaces")
                        .queryParam("api_key", apiKey).queryParam("parkCode", parkCodeValue);
                String amenitiesUrl = amenitiesURI.toUriString();
                ResponseEntity<String> amenitiesResponse = restTemplate.getForEntity(amenitiesUrl, String.class);

                //Parse the response and get the amenities for the current park code.
                JsonNode amenitiesJson = ObjectMapper.readTree(amenitiesResponse.getBody());
                List<String> amenitiesList = new ArrayList<>();
                for(JsonNode dataArray : amenitiesJson.path("data"))
                {
                    for(JsonNode amenityObject : dataArray)
                    {
                        String amenityName = amenityObject.path("name").asText();
                        amenitiesList.add(amenityName);
                    }
                }
                parkAmenitiesMap.put(parkCodeValue, amenitiesList); //Map the park code to the list of amenities.
            }

            //Create a new JSON object to store the combined data.
            ObjectNode combinedJson = ObjectMapper.createObjectNode();
            combinedJson.set("total", parksJson.path("total")); //Add the total to the combined JSON object.
            combinedJson.set("limit", parksJson.path("limit")); //Add the limit to the combined JSON object.
            combinedJson.set("start", parksJson.path("start")); //Add the start to the combined JSON object.
            combinedJson.set("parks", parksJson.path("data")); //Add the list of parks to the combined JSON object.
            combinedJson.set("amenities", ObjectMapper.valueToTree(parkAmenitiesMap)); //Add the list of amenities to the combined JSON object.

            return ResponseEntity.ok(combinedJson.toString()); //Return the combined JSON object as a string.
        }
        catch (Exception e)
        {
            System.out.println("Failed to retrieve park data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve park data");
        }
    }
}
package edu.usc.csci310.project.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class ParkActivitiesController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${apiKey}")
    private String apiKey;
    @GetMapping("/api/getParkActivities")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> getParkActivities(@RequestParam(required = false) String parkCode, @RequestParam(required = false) String q, @RequestParam(defaultValue = "10") Integer limit) {
        // Check if parkCode is provided and if it meets the length requirements
        if (parkCode != null && !parkCode.isEmpty() && (parkCode.length() < 4 || parkCode.length() > 10)) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Park code must be between 4 and 10 characters");
        }

        String baseUrl = "https://developer.nps.gov/api/v1/activities/parks";

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("api_key", apiKey).queryParam("limit", limit);

        if (parkCode != null && !parkCode.isEmpty()) {
            uriBuilder.queryParam("parkCode", parkCode);
        }
        if (q != null && !q.isEmpty()) {
            uriBuilder.queryParam("q", q);
        }

        String uri = uriBuilder.toUriString();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            System.out.println("Failed to retrieve activities data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve activities data");
        }
    }
}
package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.service.UserService;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;

@RestController
class SuggestController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${apiKey}")
    private String apiKey;

    @GetMapping("/api/suggestParks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> suggest(
            @RequestParam(defaultValue = "") String usernames,
            @RequestHeader("Authorization") String fullToken) {
        List<List<String>> parkList = new ArrayList<>();
        String[] usernameList = usernames.split(",");

        for (String name : usernameList) {
            if (!userService.isUserExist(name)) {
                String errorMessage = "ERROR: user: " + name + " does not exist";
                return ResponseEntity.badRequest().body("Error: " + errorMessage);
            }
            String favorites = userService.getFavorites(name);
            if (favorites.isEmpty()) {
                String errorMessage = "ERROR: user: " + name + " has no favorite parks";
                return ResponseEntity.badRequest().body("Error: " + errorMessage);
            }
            String[] favoritesList = favorites.split(",");
            List<String> parkCodes = Arrays.asList(favoritesList);
            parkList.add(parkCodes);
        }

        List<String> suggestedParks = new ArrayList<>();

        // Case 1: Return the highest-ranked park from the intersection of all user lists
        Set<String> intersection = new HashSet<>(parkList.get(0));
        for (int i = 1; i < parkList.size(); i++) {
            intersection.retainAll(parkList.get(i));
        }
        if (!intersection.isEmpty()) {
            suggestedParks.addAll(intersection);
        } else {
            // Case 2: Return the highest-ranked park from the intersection of subsets of user lists
            Set<String> maxIntersection = new HashSet<>();
            for (int i = 0; i < parkList.size(); i++) {
                for (int j = i + 1; j < parkList.size(); j++) {
                    Set<String> subsetIntersection = new HashSet<>(parkList.get(i));
                    subsetIntersection.retainAll(parkList.get(j));
                    if (subsetIntersection.size() > maxIntersection.size()) {
                        maxIntersection = subsetIntersection;
                    }
                }
            }
            if (!maxIntersection.isEmpty()) {
                suggestedParks.addAll(maxIntersection);
            } else {
                // Case 3: Return the highest-ranked park from the current user's favorites list
                suggestedParks.addAll(parkList.get(0));
            }
        }

        // Return the static top answer
        String suggestedPark = suggestedParks.get(0).substring(1);

        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;

        // Configure the RestTemplate to trust all certificates
        SSLContext sslContext;
        try {
            sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create SSL context. Exception: " + e.getMessage());
        }

        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                    ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                }
                super.prepareConnection(connection, httpMethod);
            }
        });

        // Make the API call and handle the response
        ResponseEntity<String> apiResponse = restTemplate.exchange(
                "https://developer.nps.gov/api/v1/parks?parkCode=" + suggestedPark +"&api_key=" + apiKey,
                HttpMethod.GET,
                null,
                String.class
        );

        if (apiResponse.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(apiResponse.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get park info. Status code: " + apiResponse.getStatusCodeValue());
        }
    }
}

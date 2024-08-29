package edu.usc.csci310.project.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usc.csci310.project.model.UpdateFavoriteRankRequest;
import edu.usc.csci310.project.service.UserService;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;

@RestController
public class FavoritesController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @GetMapping("/api/getFavorites")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> getFavorites(@RequestParam String username, @RequestHeader("Authorization") String token) {
        if (username == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is required.");
        }
        if (!userService.isUserExist(username)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        System.out.println("Header JWT Token: " + token);

        String favorites = userService.getFavorites(username);

        if (favorites.isEmpty()) {
            System.out.println("HERE");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No favorites found.");
        }
        // Split the favorites string by comma and extract the park codes
        String[] favoritesList = Arrays.stream(favorites.split(","))
                .map(s -> s.replaceAll("^\\d+", ""))
                .toArray(String[]::new);

        for (String s : favoritesList) {
            System.out.println("Park code: " + s);
        }

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

        List<JsonNode> results = new ArrayList<>();

        for (String parkCode : favoritesList) {
            // call /getParks?parkCode=parkCode
            String baseUrl = "https://localhost:8080";
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/api/getParks")
                    .queryParam("parkCode", parkCode);

            // add a jwt token to the header of the request
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", token);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode parkJson = objectMapper.readTree(response.getBody());
                    results.add(parkJson);
                } else {
                    // Handle non-successful response
                    return ResponseEntity.status(response.getStatusCode())
                            .body("Failed to get park info. Status code: " + response.getStatusCode());
                }
            } catch (Exception e) {
                // Handle exception
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to get park info. Exception: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(results.toString());
    }

    @PostMapping("/api/addToFavorites")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> addToFavorites(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String parkCode = requestBody.get("parkCode");

        // Call the userService to add the favorite park for the user
        boolean success = userService.addFavorite(username, parkCode);

        if (success) {
            return ResponseEntity.ok("Park added to favorites successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Park already in favorites.");
        }
    }

    @PostMapping("/api/checkFavorites")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Boolean> checkFavorites(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String parkCode = requestBody.get("parkCode");

        String fav = userService.getFavorites(username);

        Boolean isFavorite = fav.contains(parkCode);

        return ResponseEntity.ok(isFavorite);
    }

    @PostMapping("/api/deleteParkFromFavorites")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> deleteParkFromFavorites(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String parkCode = requestBody.get("parkCode");

        // Call the userService to delete the favorite park for the user
        boolean success = userService.deleteFavorite(username, parkCode);

        if (success) {
            return ResponseEntity.ok("Park removed from favorites successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Park not found in favorites.");
        }
    }

    @PostMapping("/api/deleteAllFavorites")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> deleteAllFavorites(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");

        // Call the userService to delete all favorites and set the user's list to private
        boolean success = userService.deleteAllFavorites(username);

        if (success) {
            return ResponseEntity.ok("All favorites deleted, and list set to private.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserEntity not found.");
        }
    }

    @PostMapping("/api/togglePrivate")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> togglePrivate(@RequestParam("username") String username) {
        // Call the userService to toggle the user's isPrivate status
        boolean success = userService.togglePrivate(username);

        if (success) {
            return ResponseEntity.ok("UserEntity's private status toggled successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

//    @PostMapping("/api/isPrivate")
//    @PreAuthorize("hasAuthority('USER')")
//    public ResponseEntity<Boolean> isPrivate(@RequestBody Map<String, String> requestBody) {
//        String username = requestBody.get("username");
//        Boolean isPrivate = userService.isPrivate(username);
//        if (isPrivate != null) {
//            return ResponseEntity.ok(isPrivate);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }

    @PostMapping("/api/isPrivate")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Boolean> isPrivate(@RequestParam(defaultValue = "username") String username) {
        Boolean isPrivate = userService.isPrivate(username);
        if (isPrivate != null) {
            return ResponseEntity.ok(isPrivate);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/api/updateFavoriteRank")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> updateFavoriteRank(@RequestBody UpdateFavoriteRankRequest request) {
        String username = request.getUsername();
        String parkCode = request.getParkCode();
        String rankChange = request.getRankChange();

        // Call the userService to update the rank of the favorite park for the user
        boolean success = userService.updateFavoriteRank(username, parkCode, rankChange);

        if (success) {
            return ResponseEntity.ok("Favorite rank updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update favorite rank.");
        }
    }
}
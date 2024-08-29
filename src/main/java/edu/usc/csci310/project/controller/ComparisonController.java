package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@RestController
public class ComparisonController {
    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${apiKey}")
    private String apiKey;

    @GetMapping("/api/compareParks")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Map<String, Object>> compare(@RequestParam(defaultValue="") String usernames, @RequestHeader("Authorization") String token) {
        Map<String, Integer> parkFrequency = new HashMap<>();
        String[] usernameList = usernames.split(",");
        int totalUsers = usernameList.length;

        for (String name : usernameList) {
            if (!userService.isUserExist(name)) {
                String errorMessage = "ERROR: user: " + name + " does not exist";
                Map<String, Object> error = new HashMap<>();
                error.put("error", errorMessage);
                return ResponseEntity.badRequest().body(error);
            }
            String favorites = userService.getFavorites(name);
            if (favorites.isEmpty()) {
                String errorMessage = "ERROR: user: " + name + " has no favorite parks";
                Map<String, Object> error = new HashMap<>();
                error.put("error", errorMessage);
                return ResponseEntity.badRequest().body(error);
            }
            String[] favoritesList = favorites.split(",");
            for (String favorite : favoritesList) {
                String parkCode = favorite.substring(1);
                parkFrequency.put(parkCode, parkFrequency.getOrDefault(parkCode, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sortedParks = new ArrayList<>(parkFrequency.entrySet());
        sortedParks.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        List<Map<String, Object>> parkDataList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedParks) {
            String parkCode = entry.getKey();
            int frequency = entry.getValue();

            // Get the usernames who have the current park in their favorites
            StringBuilder users = new StringBuilder();
            for (String name : usernameList) {
                if (userService.getFavorites(name).contains(parkCode)) {
                    String add = new String(Base64.getDecoder().decode(name));
                    users.append(add).append(" ");
                }
            }

            String baseUrl = "https://localhost:8080";
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/api/getParks")
                    .queryParam("api_key", apiKey)
                    .queryParam("parkCode", parkCode);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> parkData = new HashMap<>();
                parkData.put("parkCode", parkCode);
                parkData.put("frequency", frequency);
                parkData.put("favusernames", users);
                parkData.put("data", response.getBody());
                parkDataList.add(parkData);
            }
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("users", String.join(",", usernameList));
        responseMap.put("total", totalUsers);
        responseMap.put("parkData", parkDataList);

        return ResponseEntity.ok(responseMap);
    }
}
package edu.usc.csci310.project.model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateFavoriteRankRequestTest {
    @Test
    void testConstructor() {
        UpdateFavoriteRankRequest request = new UpdateFavoriteRankRequest("john_doe", "yose", "+");
        assertEquals("john_doe", request.getUsername());
        assertEquals("yose", request.getParkCode());
        assertEquals("+", request.getRankChange());
    }

    @Test
    void testSetUsername() {
        UpdateFavoriteRankRequest request = new UpdateFavoriteRankRequest("john_doe", "yose", "+");
        request.setUsername("jane_doe");
        assertEquals("jane_doe", request.getUsername());
    }

    @Test
    void testSetParkCode() {
        UpdateFavoriteRankRequest request = new UpdateFavoriteRankRequest("john_doe", "yose", "+");
        request.setParkCode("jomu");
        assertEquals("jomu", request.getParkCode());
    }

    @Test
    void testSetRankChange() {
        UpdateFavoriteRankRequest request = new UpdateFavoriteRankRequest("john_doe", "yose", "+");
        request.setRankChange("-");
        assertEquals("-", request.getRankChange());
    }
}
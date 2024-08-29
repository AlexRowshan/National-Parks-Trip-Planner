package edu.usc.csci310.project.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {
    @Test
    void testEmptyConstructor() {
        UserEntity userEntity = new UserEntity();
        assertNull(userEntity.getUsername());
        assertNull(userEntity.getPassword());
        assertEquals(userEntity.getFavorites(), ""); // Adjusted for the correct initialization
    }

    @Test
    void getUsername() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        assertEquals("Bob", userEntity.getUsername());
    }

    @Test
    void getPassword() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        assertEquals("Builder", userEntity.getPassword());
    }

    @Test
    void getFavorites_emptyFavorites() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        assertEquals("", userEntity.getFavorites());
    }

    @Test
    void addFavorite_singleFavorite() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        userEntity.addFavorite("ABC");
        assertEquals("1ABC", userEntity.getFavorites()); // Adjusted to include the rank
    }

    @Test
    void addFavorite_multipleFavorites() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        userEntity.addFavorite("ABC");
        userEntity.addFavorite("DEF");
        userEntity.addFavorite("GHI");
        assertEquals("1ABC,2DEF,3GHI", userEntity.getFavorites()); // Adjusted for rank inclusion
    }

    @Test
    void addFavorite_duplicateFavorite() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        userEntity.addFavorite("ABC");
        userEntity.addFavorite("DEF");
        // Trying to add "ABC123" again should not change the favorites since it's a duplicate
        userEntity.addFavorite("ABC");
        assertEquals("1ABC,2DEF", userEntity.getFavorites()); // Remains unchanged after attempting to add duplicate
    }

    @Test
    void addFavorite_withNonSequentialRank() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        userEntity.addFavorite("XYZ");
        userEntity.addFavorite("ABC");
        userEntity.addFavorite("DEF");

        // The rank for "DEF" should be 3, even though the maximum rank before adding it was 1
        assertEquals("1XYZ,2ABC,3DEF", userEntity.getFavorites());
    }

    @Test
    void removeFavorite_ParkIsRemoved() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        // Simulate adding favorites
        userEntity.addFavorite("XYZ");
        userEntity.addFavorite("ABC");
        userEntity.addFavorite("DEF");

        // Attempt to remove one favorite
        boolean result = userEntity.removeFavorite("ABC");

        assertTrue(result, "Park should be removed");
        // Verify the removal (assuming a getFavorites method exists and works as expected)
        assertFalse(userEntity.getFavorites().contains("ABC"), "ABC should not be present in favorites after removal.");
    }

    @Test
    void removeFavorite_ParkIsNotRemovedWhenNotPresent() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        // Simulate adding favorites
        userEntity.addFavorite("XYZ");
        userEntity.addFavorite("DEF");

        // Attempt to remove a park that was never added
        boolean result = userEntity.removeFavorite("ABC");

        assertFalse(result, "Park should not be removed because it is not in favorites");
        // The favorites should still contain the originally added parks
        assertTrue(userEntity.getFavorites().contains("XYZ") && userEntity.getFavorites().contains("DEF"), "Favorites should remain unchanged.");
    }

    @Test
    void removeFavorite_EmptyFavorites() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        // Initially, no favorites are added

        boolean result = userEntity.removeFavorite("ABC");

        assertFalse(result, "Park should not be removed because favorites are initially empty");
        assertEquals("", userEntity.getFavorites(), "Favorites should remain empty.");
    }

    @Test
    void getIsPrivate_defaultValue() {
        UserEntity userEntity = new UserEntity();
        assertTrue(userEntity.getIsPrivate(), "Default value of isPrivate should be true");
    }

    @Test
    void getIsPrivate_afterConstructorWithParameters() {
        UserEntity userEntity = new UserEntity("Bob", "Builder");
        assertTrue(userEntity.getIsPrivate(), "Default value of isPrivate should be true after constructor with parameters");
    }

    @Test
    void setIsPrivate_toggleFromTrueToFalse() {
        UserEntity userEntity = new UserEntity();
        userEntity.setIsPrivate();
        assertFalse(userEntity.getIsPrivate(), "isPrivate should be false after calling setIsPrivate() once");
    }

    @Test
    void setIsPrivate_toggleFromFalseToTrue() {
        UserEntity userEntity = new UserEntity();
        userEntity.setIsPrivate();
        userEntity.setIsPrivate();
        assertTrue(userEntity.getIsPrivate(), "isPrivate should be true after calling setIsPrivate() twice");
    }

    @Test
    void testGetUsername() {
        UserEntity userEntity = new UserEntity("john_doe", "password");
        assertEquals("john_doe", userEntity.getUsername());
    }

    @Test
    void testGetParkCode() {
        UserEntity userEntity = new UserEntity("john_doe", "password");
        userEntity.addFavorite("yose");
        assertTrue(userEntity.getFavorites().contains("yose"));
    }

    @Test
    void testDefaultPrivate(){
        UserEntity userEntity = new UserEntity("john_doe", "password");
        userEntity.setIsPrivate();
        userEntity.defaultPrivate();
        assertTrue(userEntity.getIsPrivate());
    }
}

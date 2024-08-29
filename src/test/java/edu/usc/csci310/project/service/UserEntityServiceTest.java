
package edu.usc.csci310.project.service;
import edu.usc.csci310.project.entity.UserEntity;
import edu.usc.csci310.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserEntityServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService();
        userService.userRepository = userRepository;
    }

    @Test
    public void testCreateUser_Successful() {
        // Arrange
        UserEntity newUserEntity = new UserEntity("testUser", "password");
        when(userRepository.findById(newUserEntity.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(newUserEntity)).thenReturn(newUserEntity);

        // Act
        UserEntity createdUserEntity = userService.createUser(newUserEntity);

        // Assert
        assertEquals(newUserEntity, createdUserEntity);
        verify(userRepository, times(1)).findById(newUserEntity.getUsername());
        verify(userRepository, times(1)).save(newUserEntity);
    }

    @Test
    public void testCreateUser_UsernameAlreadyTaken() {
        // Arrange
        UserEntity existingUserEntity = new UserEntity("existingUserEntity", "password");
        when(userRepository.findById(existingUserEntity.getUsername())).thenReturn(Optional.of(existingUserEntity));

        // Act
        UserEntity createdUserEntity = userService.createUser(existingUserEntity);

        // Assert
        assertNull(createdUserEntity);
        verify(userRepository, times(1)).findById(existingUserEntity.getUsername());
        verify(userRepository, never()).save(existingUserEntity);
    }

    @Test
    public void testGetUserByUsername_UserDoesNotExist() {
        // Arrange
        String username = "nonExistingUser";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        // Act
        Optional<UserEntity> result = userService.getUserByUsername(username);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(username);
    }

    @Test
    public void deleteUserTest()
    {
        //Try to add a new user then delete it.
        String testUsername = "username_that_doesn't_exist22323412358998^&*(*&^&*(##)@";
        UserEntity userEntityToDelete = new UserEntity(testUsername, "password");
        when(userRepository.findById(testUsername)).thenReturn(Optional.of(userEntityToDelete));

        assertTrue(userService.deleteUser(testUsername));
    }

    @Test
    public void deleteNonExistingUserTest()
    {
        String testUsername = "username_that_doesn't_exist22323412358998^&*(*&^&*(##)@";
        when(userRepository.findById(testUsername)).thenReturn(Optional.empty());
        assertFalse(userService.deleteUser(testUsername));
    }

    @Test
    public void testGetUserByUsername_UserExists() {
        // Arrange
        String username = "existingUserEntity";
        UserEntity existingUserEntity = new UserEntity(username, "password");
        when(userRepository.findById(username)).thenReturn(Optional.of(existingUserEntity));

        // Act
        Optional<UserEntity> result = userService.getUserByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(existingUserEntity, result.get());
        verify(userRepository, times(1)).findById(username);
    }

    @Test
    public void testAddFavorite_UserExists() {
        // Arrange
        String username = "existingUserEntity";
        String parkCode = "ABC123";
        UserEntity existingUserEntity = new UserEntity(username, "password");
        when(userRepository.findById(username)).thenReturn(Optional.of(existingUserEntity));

        // Act
        boolean result = userService.addFavorite(username, parkCode);

        // Assert
        assertTrue(result);
        assertTrue(existingUserEntity.getFavorites().contains(parkCode));
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, times(1)).save(existingUserEntity);
    }

    @Test
    public void testAddFavorite_UserDoesNotExist() {
        // Arrange
        String username = "nonExistingUser";
        String parkCode = "ABC123";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.addFavorite(username, parkCode);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testAddFavorite_AlreadyExists() {

        String username = "existingUserEntity";
        String parkCode = "ABC123";
        UserEntity existingUserEntity = new UserEntity(username, "password");


        existingUserEntity.addFavorite(parkCode); // Pretend this adds the favorite successfully the first time

        when(userRepository.findById(username)).thenReturn(Optional.of(existingUserEntity));


        boolean result = userService.addFavorite(username, parkCode);


        assertFalse(result);
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, never()).save(existingUserEntity);
    }

    @Test
    public void testGetFavorites_existingUser() {
        String username = "existingUserEntity";
        String parkCode = "ABC123";
        UserEntity existingUserEntity = new UserEntity(username, "password");

        existingUserEntity.addFavorite(parkCode);
        when(userRepository.findById(username)).thenReturn(Optional.of(existingUserEntity));

        String result = userService.getFavorites(username);

        assertEquals(parkCode, result.substring(1));
    }

    @Test
    public void testGetFavorites_nonExistingUser() {
        String username = "nonExistingUser";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        String result = userService.getFavorites(username);

        assertNull(result);
    }

    @Test
    void deleteFavorite_WhenUserExistsAndParkIsRemoved() {
        // Arrange
        String username = "user1";
        String parkCode = "park123";
        UserEntity mockUserEntity = mock(UserEntity.class);
        when(mockUserEntity.removeFavorite(parkCode)).thenReturn(true);
        when(userRepository.findById(username)).thenReturn(Optional.of(mockUserEntity));

        // Act
        boolean result = userService.deleteFavorite(username, parkCode);

        // Assert
        assertTrue(result);
        verify(userRepository).save(mockUserEntity);
        verify(mockUserEntity).removeFavorite(parkCode);
    }

    @Test
    void deleteFavorite_WhenUserExistsButParkIsNotRemoved() {
        // Arrange
        String username = "user1";
        String parkCode = "nonexistentPark";
        UserEntity mockUserEntity = mock(UserEntity.class);
        when(mockUserEntity.removeFavorite(parkCode)).thenReturn(false);
        when(userRepository.findById(username)).thenReturn(Optional.of(mockUserEntity));

        // Act
        boolean result = userService.deleteFavorite(username, parkCode);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(mockUserEntity);
        verify(mockUserEntity).removeFavorite(parkCode);
    }

    @Test
    void deleteFavorite_WhenUserDoesNotExist() {
        // Arrange
        String username = "user1";
        String parkCode = "park123";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.deleteFavorite(username, parkCode);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testIsPrivate_UserExists() {
        // Arrange
        String username = "existingUserEntity";
        UserEntity existingUserEntity = new UserEntity(username, "password");
        existingUserEntity.setIsPrivate(); // Set isPrivate to false
        when(userRepository.findById(username)).thenReturn(Optional.of(existingUserEntity));

        // Act
        Boolean result = userService.isPrivate(username);

        // Assert
        assertNotNull(result);
        assertFalse(result);
        verify(userRepository, times(1)).findById(username);
    }

    @Test
    public void testIsPrivate_UserDoesNotExist() {
        // Arrange
        String username = "nonExistingUser";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        // Act
        Boolean result = userService.isPrivate(username);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(username);
    }

    @Test
    public void testTogglePrivate_UserExists() {
        // Arrange
        String username = "existingUserEntity";
        UserEntity existingUserEntity = new UserEntity(username, "password");
        when(userRepository.findById(username)).thenReturn(Optional.of(existingUserEntity));

        // Act
        boolean result = userService.togglePrivate(username);

        // Assert
        assertTrue(result);
        assertFalse(existingUserEntity.getIsPrivate());
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, times(1)).save(existingUserEntity);
    }

    @Test
    public void testTogglePrivate_UserDoesNotExist() {
        // Arrange
        String username = "nonExistingUser";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.togglePrivate(username);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void updateFavoriteRank_UserNotFound() {
        // Arrange
        String username = "nonexistentUser";
        String parkCode = "yose";
        String rankChange = "+";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void updateFavoriteRank_ParkNotFound() {
        // Arrange
        String username = "john_doe";
        String parkCode = "nonexistentPark";
        String rankChange = "+";
        UserEntity userEntity = new UserEntity(username, "password");
        userEntity.setFavorites("1yose,2jomu,3jell");
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void updateFavoriteRank_ParkAlreadyAtTop() {
        // Arrange
        String username = "john_doe";
        String parkCode = "yose";
        String rankChange = "+";
        UserEntity userEntity = new UserEntity(username, "password");
        userEntity.setFavorites("1yose,2jomu,3jell");
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void updateFavoriteRank_ParkAlreadyAtBottom() {
        // Arrange
        String username = "john_doe";
        String parkCode = "jell";
        String rankChange = "-";
        UserEntity userEntity = new UserEntity(username, "password");
        userEntity.setFavorites("1yose,2jomu,3jell");
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void updateFavoriteRank_InvalidRankChange() {
        // Arrange
        String username = "john_doe";
        String parkCode = "yose";
        String rankChange = "invalid";
        UserEntity userEntity = new UserEntity(username, "password");
        userEntity.setFavorites("1yose,2jomu,3jell");
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void updateFavoriteRank_MoveUp() {
        // Arrange
        String username = "john_doe";
        String parkCode = "jomu";
        String rankChange = "+";
        UserEntity userEntity = new UserEntity(username, "password");
        userEntity.setFavorites("1yose,2jomu,3jell");
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertTrue(result);
        assertEquals("1jomu,2yose,3jell", userEntity.getFavorites());
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    public void updateFavoriteRank_MoveDown() {
        // Arrange
        String username = "john_doe";
        String parkCode = "yose";
        String rankChange = "-";
        UserEntity userEntity = new UserEntity(username, "password");
        userEntity.setFavorites("1yose,2jomu,3jell");
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertTrue(result);
        assertEquals("1jomu,2yose,3jell", userEntity.getFavorites());
        verify(userRepository, times(1)).findById(username);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    public void updateFavoriteRank_MoveDownAtBottom_NoChange() {
        // Arrange
        String username = "john_doe";
        String parkCode = "jell"; // The park code for the last park in the favorites list
        String rankChange = "-"; // Attempting to move down the last park
        UserEntity userEntity = new UserEntity(username, "password");
        userEntity.setFavorites("1yose,2jomu,3jell"); // 'jell' is already at the bottom
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertTrue(result); // Method should return true indicating it processed the request
        assertEquals("1yose,2jomu,3jell", userEntity.getFavorites()); // Favorites should remain unchanged
        verify(userRepository, times(1)).findById(username); // Confirm findById was called once
        verify(userRepository, never()).save(userEntity); // Confirm save was never called since there's no change
    }

    @Test
    public void updateFavoriteRank_ParkAtBottomMoveDown_NoChange() {
        // Arrange
        String username = "john_doe";
        String parkCode = "jell"; // The park code for the last park in the favorites list
        String rankChange = "-"; // Attempting to move down the last park
        UserEntity userEntity = new UserEntity(username, "password");
        userEntity.setFavorites("1yose,2jomu,3jell"); // 'jell' is already at the bottom
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertTrue(result); // Method should return true indicating it processed the request
        assertEquals("1yose,2jomu,3jell", userEntity.getFavorites()); // Favorites should remain unchanged
        verify(userRepository, times(1)).findById(username); // Confirm findById was called once
        verify(userRepository, never()).save(userEntity); // Confirm save was never called since there's no change
    }
    
    @Test
    void testIsUserExist_UserExists() {
        // Arrange
        String username = "existingUserEntity";
        UserEntity existingUserEntity = new UserEntity(username, "password");
        when(userRepository.findById(username)).thenReturn(Optional.of(existingUserEntity));

        // Act
        boolean result = userService.isUserExist(username);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findById(username);
    }

    @Test
    void testIsUserExist_UserDoesNotExist() {
        // Arrange
        String username = "nonExistingUser";
        when(userRepository.findById(username)).
                thenReturn(Optional.empty());

        // Act
        boolean result = userService.isUserExist(username);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findById(username);
    }

    @Test
    public void updateFavoriteRank_ParkAtBottomMoveUp_UpdatedFavorites() {
        // Arrange
        String username = "john_doe";
        String parkCode = "jell"; // The park code for the last park in the favorites list
        String rankChange = "+"; // Attempting to move up the last park
        UserEntity userEntity = new UserEntity(username, "password");
        userEntity.setFavorites("1yose,2jomu,3jell"); // 'jell' is at the bottom
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = userService.updateFavoriteRank(username, parkCode, rankChange);

        // Assert
        assertTrue(result); // Method should return true indicating it processed the request
        assertEquals("1yose,2jell,3jomu", userEntity.getFavorites()); // Favorites should be updated with 'jell' moved up
        verify(userRepository, times(1)).findById(username); // Confirm findById was called once
        verify(userRepository, times(1)).save(userEntity); // Confirm save was called once to persist the changes
    }
    
    @Test
    public void testIsPrivate_UserExistsAndIsPrivate() {
        // Arrange
        String username = "privateUser";
        UserEntity mockUserEntity = mock(UserEntity.class);
        when(mockUserEntity.getIsPrivate()).thenReturn(true);
        when(userRepository.findById(username)).thenReturn(Optional.of(mockUserEntity));

        // Act
        Boolean isPrivate = userService.isPrivate(username);

        // Assert
        assertTrue(isPrivate);
        verify(userRepository, times(1)).findById(username);
    }

    @Test
    public void testIsPrivate_UserExistsAndIsNotPrivate() {
        // Arrange
        String username = "publicUser";
        UserEntity mockUserEntity = mock(UserEntity.class);
        when(mockUserEntity.getIsPrivate()).thenReturn(false);
        when(userRepository.findById(username)).thenReturn(Optional.of(mockUserEntity));

        // Act
        Boolean isPrivate = userService.isPrivate(username);

        // Assert
        assertFalse(isPrivate);
        verify(userRepository, times(1)).findById(username);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        String username = "testuser";
        UserEntity userEntity = new UserEntity(username, "Aa1");
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(userEntity.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER")));
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        String username = "john";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User " + username + " does not exist...");
    }

    @Test
    void deleteAllFavorites_WhenUserExistsAndParkIsRemoved() {
        // Arrange
        String username = "user1";
        String parkCode = "park123";
        UserEntity mockUserEntity = mock(UserEntity.class);
        when(userRepository.findById(username)).thenReturn(Optional.of(mockUserEntity));
        mockUserEntity.defaultPrivate();

        // Act
        boolean result = userService.deleteAllFavorites(username);

        // Assert
        assertTrue(result);
        verify(userRepository).save(mockUserEntity);
    }
    @Test
    void deleteAllFavorite_WhenUserDoesNotExist() {
        // Arrange
        String username = "user1";
        String parkCode = "park123";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.deleteAllFavorites(username);

        // Assert
        assertFalse(result);
    }
}

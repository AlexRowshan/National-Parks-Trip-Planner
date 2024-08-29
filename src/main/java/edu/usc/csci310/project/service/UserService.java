package edu.usc.csci310.project.service;

import edu.usc.csci310.project.entity.UserEntity;
import edu.usc.csci310.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    public UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findById(s);
        if (user.isPresent()) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("USER"));
            return new User(user.get().getUsername(), user.get().getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User " + s + " does not exist...");
        }
    }

    // Creates a new userEntity in the database if the username is not already taken
    public UserEntity createUser(UserEntity userEntity) {
        // Check if the username is already taken
        Optional<UserEntity> existingUser = userRepository.findById(userEntity.getUsername());
        if (existingUser.isPresent()) {
            return null;
        }
        // If the username is not taken, save the userEntity to the database
        System.out.println("Creating user: " + userEntity.getUsername());
        return userRepository.save(userEntity);
    }

    public boolean deleteUser(String username)
    {
        //Find if the user exists.
        Optional<UserEntity> existingUser = userRepository.findById(username);

        if(existingUser.isPresent())
        {
            userRepository.delete(existingUser.get());
            return true;
        } else {
            return false;
        }
    }

    // Retrieves a user by username from the database
    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findById(username);
    }

    public boolean addFavorite(String username, String parkCode) {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            if(userEntity.addFavorite(parkCode)){
                userRepository.save(userEntity);
                return true;
            }
            return false;
        }
        return false;
    }

    public String getFavorites(String username) {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            return userEntity.getFavorites();
        }
        return null;
    }

    public boolean deleteFavorite(String username, String parkCode) {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            if (userEntity.removeFavorite(parkCode)) {
                userRepository.save(userEntity);
                return true;
            }
        }
        return false;
    }

    public boolean deleteAllFavorites(String username) {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            userEntity.setFavorites("");
            userEntity.defaultPrivate();
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    public boolean isUserExist(String username) {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        return optionalUser.isPresent();
    }

    public boolean togglePrivate(String username) {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            userEntity.setIsPrivate();
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    public Boolean isPrivate(String username) {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            return userEntity.getIsPrivate();
        }
        return null;
    }

    public boolean updateFavoriteRank(String username, String parkCode, String rankChange) {
        Optional<UserEntity> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            String favorites = userEntity.getFavorites();
            String[] favoritesArray = favorites.split(",");

            int currentIndex = -1;
            for (int i = 0; i < favoritesArray.length; i++) {
                if (favoritesArray[i].contains(parkCode)) {
                    currentIndex = i;
                    break;
                }
            }

            if (currentIndex == -1) {
                // Park not found in favorites
                return false;
            }

            if (currentIndex == favoritesArray.length - 1) {
                if(rankChange.equals("-")){
                    // Park is already at the top or bottom, no change needed
                    return true;
                }
            }

            if ((currentIndex == 0 && rankChange.equals("+"))) {
                // Park is already at the top or bottom, no change needed
                return true;
            }




            int swapIndex;
            if (rankChange.equals("+")) {
                swapIndex = currentIndex - 1;
            } else if (rankChange.equals("-")) {
                swapIndex = currentIndex + 1;
            } else {
                // Invalid rankChange value
                return false;
            }

            // Swap the favorite park with the one at the swap index
            String temp = favoritesArray[currentIndex];
            favoritesArray[currentIndex] = favoritesArray[swapIndex];
            favoritesArray[swapIndex] = temp;

            // Correct the ranks after swapping
            for (int i = 0; i < favoritesArray.length; i++) {
                String[] parts = favoritesArray[i].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                favoritesArray[i] = (i + 1) + parts[1];
            }

            // Update the favorites string
            String updatedFavorites = String.join(",", favoritesArray);
            userEntity.setFavorites(updatedFavorites);
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }
}

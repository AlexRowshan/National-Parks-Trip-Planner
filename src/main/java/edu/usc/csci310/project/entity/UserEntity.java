package edu.usc.csci310.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserEntity {
    @Id
    private String username;
    private String password;
    private String favorites;
    private Boolean isPrivate = true;

    public UserEntity() {
        this.favorites = "";
        this.isPrivate = true;
        // Initialize favorites to an empty string
    }

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
        this.favorites = "";
        this.isPrivate = true;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFavorites() {
        return favorites;
    }

    public Boolean getIsPrivate(){return isPrivate;}

    public void setIsPrivate() {
        this.isPrivate = !this.isPrivate;
    }
    public void defaultPrivate() {
        this.isPrivate = true;
    }

    public void setFavorites(String favorites) {
        this.favorites = favorites;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean addFavorite(String parkCode) {
        if (!favorites.contains(parkCode)) {
            int rank = favorites.isEmpty() ? 1 : getMaxRank() + 1;
            if (!favorites.isEmpty()) {
                favorites += ",";
            }
            favorites += rank + parkCode;
            return true;
        }
        else{
            return false;
        }
    }

    private int getMaxRank() {
        String[] favoritesArray = favorites.split(",");
        int maxRank = 0;
        for (String favorite : favoritesArray) {
            int rank = Integer.parseInt(favorite.replaceAll("[^0-9]", ""));
            maxRank = rank;

        }
        return maxRank;
    }

    public boolean removeFavorite(String parkCode) {
        String[] favoritesArray = favorites.split(",");
        StringBuilder updatedFavorites = new StringBuilder();
        boolean parkRemoved = false;

        for (String favorite : favoritesArray) {
            if (!favorite.endsWith(parkCode)) {
                if (!updatedFavorites.isEmpty()) {
                    updatedFavorites.append(",");
                }
                updatedFavorites.append(favorite);
            } else {
                parkRemoved = true;
            }
        }

        if (parkRemoved) {
            favorites = updatedFavorites.toString();
        }
        return parkRemoved;
    }

    public void setUsername(String s) {
        this.username = s;
    }
}
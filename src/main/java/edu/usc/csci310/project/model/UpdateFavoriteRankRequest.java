package edu.usc.csci310.project.model;

public class UpdateFavoriteRankRequest {
    private String username;
    private String parkCode;
    private String rankChange;

    // Constructor
    public UpdateFavoriteRankRequest(String username, String parkCode, String rankChange) {
        this.username = username;
        this.parkCode = parkCode;
        this.rankChange = rankChange;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getRankChange() {
        return rankChange;
    }

    public void setRankChange(String rankChange) {
        this.rankChange = rankChange;
    }
}
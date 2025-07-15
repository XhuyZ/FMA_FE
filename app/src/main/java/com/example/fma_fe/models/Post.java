package com.example.fma_fe.models;

public class Post {
    private int postId;
    private int teamId;
    private int postedByPlayerId;
    private String pitchId;
    private Integer receivingTeamId;
    private String matchTime;
    private String description;
    private String lookingFor;
    private String postStatus;
    private String imageUrl;
    private String createdAt;
    private String updatedAt;
    private String pitchName;

    public String getPitchName() { return pitchName; }
    public void setPitchName(String pitchName) { this.pitchName = pitchName; }

    // Constructors
    public Post() {}

    public Post(int postId, int teamId, int postedByPlayerId, String pitchId,
                Integer receivingTeamId, String matchTime, String description,
                String lookingFor, String postStatus, String imageUrl,
                String createdAt, String updatedAt) {
        this.postId = postId;
        this.teamId = teamId;
        this.postedByPlayerId = postedByPlayerId;
        this.pitchId = pitchId;
        this.receivingTeamId = receivingTeamId;
        this.matchTime = matchTime;
        this.description = description;
        this.lookingFor = lookingFor;
        this.postStatus = postStatus;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public int getPostedByPlayerId() { return postedByPlayerId; }
    public void setPostedByPlayerId(int postedByPlayerId) { this.postedByPlayerId = postedByPlayerId; }

    public String getPitchId() { return pitchId; }
    public void setPitchId(String pitchId) { this.pitchId = pitchId; }

    public Integer getReceivingTeamId() { return receivingTeamId; }
    public void setReceivingTeamId(Integer receivingTeamId) { this.receivingTeamId = receivingTeamId; }

    public String getMatchTime() { return matchTime; }
    public void setMatchTime(String matchTime) { this.matchTime = matchTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLookingFor() { return lookingFor; }
    public void setLookingFor(String lookingFor) { this.lookingFor = lookingFor; }

    public String getPostStatus() { return postStatus; }
    public void setPostStatus(String postStatus) { this.postStatus = postStatus; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
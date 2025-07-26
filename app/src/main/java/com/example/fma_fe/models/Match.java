package com.example.fma_fe.models;

import com.google.firebase.database.Exclude;

public class Match {
    private int id;

    @Exclude
    private String dbKey;  // field này để lưu key thật trên Firebase
    private String pitchId;
    private String startTime;
    private String status;
    private String team_id;
    private String postId;

    // Constructor mặc định (bắt buộc cho Firebase)
    public Match() {
    }

    public Match(int id, String pitchId, String startTime, String status, String team_id) {
        this.id = id;
        this.pitchId = pitchId;
        this.startTime = startTime;
        this.status = status;
        this.team_id = team_id;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPitchId() {
        return pitchId;
    }

    public void setPitchId(String pitchId) {
        this.pitchId = pitchId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    @Exclude
    public String getDbKey() { return dbKey; }
    public void setDbKey(String dbKey) { this.dbKey = dbKey; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
}

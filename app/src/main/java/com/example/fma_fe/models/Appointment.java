package com.example.fma_fe.models;

public class Appointment {
    private String id;         // ID Firebase push()
    private String pitchId;    // Lấy từ matchPost
    private String postId;     // Liên kết tới matchPost
    private String startTime;  // Thời gian bắt đầu (matchPost)
    private String status;     // Pending / Confirmed
    private String team_id;    // teamId của người tạo appointment

    // Bắt buộc cho Firebase
    public Appointment() {}

    public Appointment(String id, String pitchId, String postId, String startTime, String status, String team_id) {
        this.id = id;
        this.pitchId = pitchId;
        this.postId = postId;
        this.startTime = startTime;
        this.status = status;
        this.team_id = team_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPitchId() {
        return pitchId;
    }

    public void setPitchId(String pitchId) {
        this.pitchId = pitchId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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
}

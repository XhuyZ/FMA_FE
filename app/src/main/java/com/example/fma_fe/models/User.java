package com.example.fma_fe.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String uid;         // Firebase UID (node key), chỉ mapping thủ công
    private Integer userId;     // ID tự tăng/người dùng, là attribute trong node
    private String name;
    private String email;
    private Integer age;
    private String bio;
    private String imageUrl;
    private Long phone;
    private String position;
    private String teamId;

    public User() {}


    public User(String uid, Integer userId, String name, String email, Integer age, String bio, String imageUrl, Long phone, String position, String teamId) {
        this.uid = uid;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.age = age;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.phone = phone;
        this.position = position;
        this.teamId = teamId;
    }

    // Optional: đầy đủ constructor hoặc chỉ cần constructor rỗng
    // Viết thêm getter/setter cho từng trường

    // Getter/setter...
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getPhone() { return phone; }
    public void setPhone(Long phone) { this.phone = phone; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
}

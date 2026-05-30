package com.hometown.shots.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(schema = "shots", name = "shot_comments")
public class ShotComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shot_id", nullable = false)
    private Long shotId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String userName;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(columnDefinition = "text", nullable = false)
    private String text;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ShotComment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShotId() { return shotId; }
    public void setShotId(Long shotId) { this.shotId = shotId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

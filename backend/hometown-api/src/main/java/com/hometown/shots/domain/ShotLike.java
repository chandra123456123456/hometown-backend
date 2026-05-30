package com.hometown.shots.domain;

import jakarta.persistence.*;

@Entity
@Table(
        schema = "shots",
        name = "shot_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shot_id", "user_id"})
)
public class ShotLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shot_id", nullable = false)
    private Long shotId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public ShotLike() {}

    public ShotLike(Long shotId, Long userId) {
        this.shotId = shotId;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShotId() { return shotId; }
    public void setShotId(Long shotId) { this.shotId = shotId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}

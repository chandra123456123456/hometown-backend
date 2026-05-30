package com.hometown.shots.repo;

import com.hometown.shots.domain.ShotLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShotLikeRepository extends JpaRepository<ShotLike, Long> {
    long countByShotId(Long shotId);
    boolean existsByShotIdAndUserId(Long shotId, Long userId);
    Optional<ShotLike> findByShotIdAndUserId(Long shotId, Long userId);
}

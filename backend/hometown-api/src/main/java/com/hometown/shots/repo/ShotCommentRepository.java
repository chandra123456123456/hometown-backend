package com.hometown.shots.repo;

import com.hometown.shots.domain.ShotComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShotCommentRepository extends JpaRepository<ShotComment, Long> {
    List<ShotComment> findByShotIdOrderByIdAsc(Long shotId);
    long countByShotId(Long shotId);
}

package com.hometown.shots.repo;

import com.hometown.shots.domain.Shot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface ShotRepository extends JpaRepository<Shot, Long> {
    List<Shot> findAll(Sort sort);
}

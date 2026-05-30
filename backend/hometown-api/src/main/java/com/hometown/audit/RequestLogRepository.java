package com.hometown.audit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    List<RequestLog> findByPathContainingIgnoreCaseOrderByIdDesc(String path, Pageable pageable);

    List<RequestLog> findByOrderByIdDesc(Pageable pageable);
}

package com.hometown.order.custom.repo;

import com.hometown.order.custom.domain.CustomOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomOrderRepository extends JpaRepository<CustomOrder, Long> {

    List<CustomOrder> findByUserIdOrderByIdDesc(Long userId);

    List<CustomOrder> findAllByOrderByIdDesc();
}

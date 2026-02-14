package org.example.repository;

import org.example.model.Order;
import org.example.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.zoneId = :zoneId AND o.status = 'CREATED'")
    List<Order> findAvailableOrders(@Param("zoneId") String zoneId);
}
package org.example.repository;

import org.example.model.PendingPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PayoutRepository extends JpaRepository<PendingPayout, Long> {

    @Query("SELECT COUNT(p) > 0 FROM PendingPayout p WHERE p.orderId = :orderId")
    boolean isEventProcessed(@Param("orderId") Long orderId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO processed_events (event_id) VALUES (:eventId)", nativeQuery = true)
    void markEventProcessed(@Param("eventId") String eventId);
}
package org.example.repository;

import org.example.model.PendingPayout;
import org.example.model.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayoutRepository extends JpaRepository<PendingPayout, Long> {

    /**
     * Проверяет, было ли событие для заказа уже обработано
     *
     * @param orderId идентификатор заказа
     * @return true если событие уже обработано
     */
    @Query("""
            SELECT COUNT(p) > 0
            FROM PendingPayout p
            WHERE p.orderId = :orderId
            """)
    boolean isEventProcessed(@Param("orderId") Long orderId);

    /**
     * Находит выплату по ID заказа
     *
     * @param orderId идентификатор заказа
     * @return Optional с выплатой
     */
    @Query("""
            SELECT p
            FROM PendingPayout p
            WHERE p.orderId = :orderId
            """)
    Optional<PendingPayout> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Находит все выплаты по статусу
     *
     * @param status статус выплаты (PENDING, PROCESSED, FAILED)
     * @return список выплат
     */
    List<PendingPayout> findByStatus(PayoutStatus status);

    /**
     * Помечает событие как обработанное
     *
     * @param eventId идентификатор события
     */
    @Modifying
    @Query(value = """
            INSERT INTO processed_events (event_id)
            VALUES (:eventId)
            """, nativeQuery = true)
    void markEventProcessed(@Param("eventId") String eventId);
}
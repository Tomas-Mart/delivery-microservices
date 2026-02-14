package org.example.repository;

import org.example.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Находит все заказы в указанной зоне со статусом CREATED
     *
     * @param zoneId идентификатор зоны
     * @return список доступных заказов
     */
    @Query("""
            SELECT o FROM Order o
            WHERE o.zoneId = :zoneId
            AND o.status = 'CREATED'
            """)
    List<Order> findAvailableOrders(@Param("zoneId") String zoneId);
}
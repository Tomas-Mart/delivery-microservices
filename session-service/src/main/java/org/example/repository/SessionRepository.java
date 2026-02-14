package org.example.repository;

import org.example.model.Session;
import org.example.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Находит активную смену курьера по его ID
     *
     * @param courierId идентификатор курьера
     * @return Optional с активной сменой
     */
    @Query("""
            SELECT s FROM Session s
            WHERE s.courierId = :courierId
            AND s.status = 'ACTIVE'
            """)
    Optional<Session> findActiveByCourierId(@Param("courierId") String courierId);

    /**
     * Находит все смены по статусу
     *
     * @param status статус смены (ACTIVE, CLOSED, PAUSED)
     * @return список смен
     */
    List<Session> findByStatus(SessionStatus status);

    /**
     * Находит все смены курьера
     *
     * @param courierId идентификатор курьера
     * @return список всех смен курьера
     */
    List<Session> findByCourierId(String courierId);

    /**
     * Проверяет, есть ли у курьера активная смена
     *
     * @param courierId идентификатор курьера
     * @return true если есть активная смена
     */
    boolean existsByCourierIdAndStatus(String courierId, SessionStatus status);
}
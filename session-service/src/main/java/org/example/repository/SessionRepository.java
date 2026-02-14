package org.example.repository;

import org.example.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT s FROM Session s WHERE s.courierId = :courierId AND s.status = 'ACTIVE'")
    Optional<Session> findActiveByCourierId(@Param("courierId") String courierId);
}
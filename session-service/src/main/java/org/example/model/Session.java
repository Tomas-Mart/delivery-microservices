package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sessions", indexes = {
        @Index(name = "idx_courier_id", columnList = "courier_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_courier_status", columnList = "courier_id, status")
})
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courier_id", nullable = false, length = 50)
    private String courierId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status;

    @CreationTimestamp
    @Column(name = "start_time", nullable = false, updatable = false)
    private LocalDateTime startTime;

    @UpdateTimestamp
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
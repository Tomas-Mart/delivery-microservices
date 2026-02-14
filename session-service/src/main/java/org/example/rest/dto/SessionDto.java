package org.example.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Session;
import org.example.model.SessionStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SessionDto {
    private Long id;
    private String courierId;
    private SessionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static SessionDto fromEntity(Session session) {
        if (session == null) return null;

        SessionDto dto = new SessionDto();
        dto.setId(session.getId());
        dto.setCourierId(session.getCourierId());
        dto.setStatus(session.getStatus());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        return dto;
    }
}
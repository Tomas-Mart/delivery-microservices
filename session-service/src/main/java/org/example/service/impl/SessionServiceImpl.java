package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.CourierAlreadyHasActiveSessionException;
import org.example.exception.SessionNotFoundException;
import org.example.model.Session;
import org.example.model.SessionStatus;
import org.example.repository.SessionRepository;
import org.example.rest.dto.SessionDto;
import org.example.service.SessionService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Реализация сервиса для управления сменами курьеров
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    @Transactional
    public SessionDto start(String courierId) {
        log.info("▶️ Начало смены для курьера: {}", courierId);

        // Проверяем, нет ли уже активной смены
        sessionRepository.findActiveByCourierId(courierId)
                .ifPresent(s -> {
                    log.warn("⚠️ Курьер {} уже имеет активную смену с ID: {}", courierId, s.getId());
                    throw new CourierAlreadyHasActiveSessionException(courierId);
                });

        // Создаём новую смену
        Session session = new Session();
        session.setCourierId(courierId);
        session.setStatus(SessionStatus.ACTIVE);
        session.setStartTime(LocalDateTime.now());

        Session saved = sessionRepository.save(session);
        log.info("✅ Создана смена с ID: {} для курьера: {}", saved.getId(), courierId);

        return SessionDto.fromEntity(saved);
    }

    @Override
    public boolean isActive(Long id) {
        log.debug("🔍 Проверка активности смены ID: {}", id);

        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException(id));

        boolean active = SessionStatus.ACTIVE.equals(session.getStatus());  // Сравниваем ENUM
        log.debug("📊 Смена ID: {} активна: {}", id, active);

        return active;
    }

    @Override
    public boolean isCourierActive(String courierId) {
        log.debug("🔍 Проверка активности курьера: {}", courierId);

        boolean active = sessionRepository.findActiveByCourierId(courierId).isPresent();
        log.debug("📊 Курьер {} активен: {}", courierId, active);

        return active;
    }

    @Override
    public Optional<SessionDto> findSessionById(Long id) {
        log.debug("🔍 Поиск смены по ID: {} (опционально)", id);

        return sessionRepository.findById(id)
                .map(session -> {
                    log.debug("📦 Найдена смена: id={}, курьер={}, статус={}",
                            session.getId(), session.getCourierId(), session.getStatus());
                    return SessionDto.fromEntity(session);
                });
    }

    @Override
    @Cacheable(value = "sessions", key = "#id")
    public SessionDto getSessionById(Long id) {
        log.info("🔍 Получение смены по ID: {}", id);

        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("❌ Смена с ID {} не найдена в БД", id);
                    return new SessionNotFoundException(id);
                });

        log.debug("📦 Найдена смена: id={}, курьер={}, статус={}",
                session.getId(), session.getCourierId(), session.getStatus());

        SessionDto dto = SessionDto.fromEntity(session);
        log.debug("✅ DTO создан успешно");

        return dto;
    }
}
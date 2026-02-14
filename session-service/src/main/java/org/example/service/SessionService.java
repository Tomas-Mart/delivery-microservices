package org.example.service;

import org.example.rest.dto.SessionDto;

import java.util.Optional;

/**
 * Интерфейс сервиса для управления сменами курьеров
 */
public interface SessionService {

    /**
     * Начать новую смену для курьера
     *
     * @param courierId идентификатор курьера
     * @return DTO созданной смены
     * @throws org.example.exception.CourierAlreadyHasActiveSessionException если у курьера уже есть активная смена
     */
    SessionDto start(String courierId);

    /**
     * Проверить, активна ли смена по ID
     *
     * @param id идентификатор смены
     * @return true если смена активна
     * @throws org.example.exception.SessionNotFoundException если смена не найдена
     */
    boolean isActive(Long id);

    /**
     * Проверить, есть ли у курьера активная смена
     *
     * @param courierId идентификатор курьера
     * @return true если есть активная смена
     */
    boolean isCourierActive(String courierId);

    /**
     * Получить смену по ID (опционально)
     *
     * @param id идентификатор смены
     * @return Optional с DTO смены или пустой Optional, если смена не найдена
     */
    Optional<SessionDto> findSessionById(Long id);

    /**
     * Получить смену по ID (с кэшированием в Redis)
     *
     * @param id идентификатор смены
     * @return DTO смены
     * @throws org.example.exception.SessionNotFoundException если смена не найдена
     */
    SessionDto getSessionById(Long id);
}
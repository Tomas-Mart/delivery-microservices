package org.example.service;

import org.example.dto.OrderDto;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для управления заказами
 * Соблюдает принципы чистой архитектуры
 */
public interface OrderService {

    /**
     * Отметить заказ как доставленный (с идемпотентностью)
     *
     * @param id             идентификатор заказа
     * @param idempotencyKey ключ идемпотентности для защиты от дублей
     * @return DTO обновленного заказа
     * @throws IllegalArgumentException если заказ не найден
     * @throws IllegalStateException    если смена курьера не активна
     */
    OrderDto deliverOrder(Long id, String idempotencyKey);

    /**
     * Получить список доступных заказов для зоны (с кэшированием)
     *
     * @param zoneId идентификатор зоны
     * @return список DTO доступных заказов
     */
    List<OrderDto> getAvailableOrders(String zoneId);

    /**
     * Найти заказ по ID (опционально)
     *
     * @param id идентификатор заказа
     * @return Optional с DTO заказа
     */
    Optional<OrderDto> findOrderById(Long id);

    /**
     * Проверить, существует ли заказ с данным ID
     *
     * @param id идентификатор заказа
     * @return true если заказ существует
     */
    boolean existsById(Long id);
}
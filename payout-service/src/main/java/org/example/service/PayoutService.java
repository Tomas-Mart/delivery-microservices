package org.example.service;

import org.example.event.OrderDeliveredEvent;

import java.math.BigDecimal;

/**
 * Интерфейс сервиса для обработки выплат курьерам
 * Соблюдает принципы чистой архитектуры
 */
public interface PayoutService {

    /**
     * Обработать событие о доставке заказа и создать выплату
     *
     * @param event событие о доставке заказа
     */
    void handleOrderDelivered(OrderDeliveredEvent event);

    /**
     * Рассчитать сумму выплаты за заказ
     *
     * @param event событие о доставке заказа
     * @return сумма выплаты
     */
    BigDecimal calculatePayout(OrderDeliveredEvent event);
}
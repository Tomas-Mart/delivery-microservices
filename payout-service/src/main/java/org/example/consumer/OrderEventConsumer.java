package org.example.consumer;

import org.example.dto.OrderDeliveredEvent;

/**
 * Интерфейс Kafka консьюмера для обработки событий заказов
 */
public interface OrderEventConsumer {

    /**
     * Обработать событие о доставке заказа
     *
     * @param event событие о доставке заказа
     */
    void consumeOrderDelivered(OrderDeliveredEvent event);
}
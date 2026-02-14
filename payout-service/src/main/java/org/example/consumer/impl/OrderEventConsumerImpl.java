package org.example.consumer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.consumer.OrderEventConsumer;
import org.example.event.OrderDeliveredEvent;
import org.example.service.PayoutService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka консьюмер для обработки событий заказов
 * Отделяет транспортный уровень от бизнес-логики
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumerImpl implements OrderEventConsumer {

    private final PayoutService payoutService;

    @Override
    @KafkaListener(topics = "order-events", groupId = "payout-group")
    public void consumeOrderDelivered(OrderDeliveredEvent event) {
        log.info("📨 Kafka: получено событие о доставке заказа: {}", event);

        try {
            payoutService.handleOrderDelivered(event);
            log.debug("✅ Событие успешно обработано сервисом выплат");
        } catch (Exception e) {
            log.error("❌ Ошибка при обработке события в Kafka: {}", e.getMessage(), e);
            throw e; // Пробрасываем для повторной обработки Kafka
        }
    }
}
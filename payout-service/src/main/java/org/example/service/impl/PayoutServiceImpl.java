package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDeliveredEvent;
import org.example.model.PendingPayout;
import org.example.repository.PayoutRepository;
import org.example.service.PayoutService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Реализация сервиса для обработки выплат курьерам
 * Соблюдает принципы чистой архитектуры:
 * - Внедрение через конструктор
 * - Логирование через Lombok
 * - Транзакционность
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {

    private final PayoutRepository payoutRepository;

    @Override
    @KafkaListener(topics = "order-events", groupId = "payout-group")
    @Transactional
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        log.info("📥 Получено событие о доставке заказа: {}", event);

        try {
            // Проверка идемпотентности через таблицу processed_events
            if (payoutRepository.isEventProcessed(event.getOrderId())) {
                log.info("⏭️ Событие для заказа {} уже обработано, пропускаем", event.getOrderId());
                return;
            }

            // Создание выплаты за заказ
            // ✅ Используем фабричный метод модели
            PendingPayout payout = PendingPayout.createForOrder(
                    event.getCourierId(),
                    event.getOrderId(),
                    calculatePayout(event)
            );

            // Сохраняем выплату
            payoutRepository.save(payout);
            log.debug("💾 Выплата сохранена в БД: заказ={}, курьер={}, сумма={}, статус={}",
                    event.getOrderId(), event.getCourierId(), payout.getAmount(), payout.getStatus());

            // Отмечаем событие как обработанное
            payoutRepository.markEventProcessed("order-" + event.getOrderId());
            log.debug("✅ Событие отмечено как обработанное: order-{}", event.getOrderId());

            log.info("✅ Выплата успешно создана для заказа: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("❌ Ошибка при обработке выплаты для заказа {}: {}",
                    event.getOrderId(), e.getMessage(), e);
            throw e; // Пробрасываем для повторной обработки Kafka
        }
    }

    @Override
    public BigDecimal calculatePayout(OrderDeliveredEvent event) {
        log.debug("🧮 Расчет выплаты для заказа: {}", event.getOrderId());

        // Бизнес-логика расчёта выплаты
        // В реальном проекте здесь может быть сложная логика
        // на основе времени доставки, рейтинга курьера, бонусов и т.д.
        BigDecimal amount = BigDecimal.valueOf(100);

        log.debug("💰 Рассчитана выплата для заказа {}: {}", event.getOrderId(), amount);
        return amount; // фиксированная сумма для примера
    }
}
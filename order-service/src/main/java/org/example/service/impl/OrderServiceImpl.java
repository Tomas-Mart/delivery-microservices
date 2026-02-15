package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDto;
import org.example.dto.OrderDeliveredEvent;
import org.example.feign.SessionServiceClient;
import org.example.model.Order;
import org.example.enums.OrderStatus;
import org.example.repository.OrderRepository;
import org.example.service.OrderService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для управления заказами
 * - Внедрение через конструктор
 * - Логирование через Lombok
 * - Транзакционность
 * - Идемпотентность операций
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, OrderDeliveredEvent> kafkaTemplate;
    private final SessionServiceClient sessionClient;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderDto deliverOrder(Long id, String idempotencyKey) {
        log.info("📦 Отметка заказа {} как доставленного, ключ идемпотентности: {}", id, idempotencyKey);

        try {
            // 1. Проверка идемпотентности
            String redisKey = "idempotency:" + idempotencyKey;
            String existing = redisTemplate.opsForValue().get(redisKey);

            if (existing != null) {
                log.info("⏭️ Запрос с ключом {} уже обработан, возвращаем кэшированный результат", idempotencyKey);
                return findOrderById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + id));
            }

            // 2. Получение заказа из БД
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("❌ Заказ с ID {} не найден", id);
                        return new IllegalArgumentException("Заказ не найден: " + id);
                    });

            log.debug("📋 Найден заказ: ID={}, курьер={}, статус={}",
                    order.getId(), order.getCourierId(), order.getStatus());

            // 3. Проверка активности смены через Feign клиент
            try {
                if (!sessionClient.isActive(order.getCourierId())) {
                    log.warn("⚠️ Смена курьера {} не активна", order.getCourierId());
                    throw new IllegalStateException("Смена курьера не активна");
                }
                log.debug("✅ Смена курьера {} активна", order.getCourierId());
            } catch (Exception e) {
                log.error("❌ Ошибка при проверке активности смены через SessionService: {}", e.getMessage());
                throw new IllegalStateException("Не удалось проверить активность смены", e);
            }

            // 4. Обновление статуса заказа
            order.setStatus(OrderStatus.DELIVERED);
            Order savedOrder = orderRepository.save(order);
            log.debug("💾 Статус заказа {} обновлен на DELIVERED", id);

            // 5. Сохранение ключа идемпотентности в Redis
            redisTemplate.opsForValue().set(redisKey, "processed", Duration.ofHours(24));
            log.debug("🔑 Ключ идемпотентности {} сохранен в Redis на 24 часа", redisKey);

            // 6. Публикация события в Kafka
            try {
                OrderDeliveredEvent event = new OrderDeliveredEvent(savedOrder.getId(), savedOrder.getCourierId());
                kafkaTemplate.send("order-events", event);
                log.info("📨 Событие о доставке заказа {} опубликовано в Kafka", id);
            } catch (Exception e) {
                log.error("❌ Ошибка при публикации события в Kafka: {}", e.getMessage());
                // Не прерываем транзакцию, так как заказ уже отмечен как доставленный
                // В реальном проекте можно добавить компенсационные действия
            }

            OrderDto result = OrderDto.fromEntity(savedOrder);
            log.info("✅ Заказ {} успешно отмечен как доставленный", id);

            return result;

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("❌ Бизнес-ошибка при обработке заказа {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Непредвиденная ошибка при обработке заказа {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка при обработке заказа", e);
        }
    }

    @Override
    @Cacheable(value = "availableOrders", key = "#zoneId", unless = "#result.isEmpty()")
    public List<OrderDto> getAvailableOrders(String zoneId) {
        log.info("🔍 Запрос доступных заказов для зоны: {}", zoneId);

        try {
            List<Order> orders = orderRepository.findAvailableOrders(zoneId);
            log.debug("📋 Найдено {} заказов в зоне {}", orders.size(), zoneId);

            List<OrderDto> result = orders.stream()
                    .map(OrderDto::fromEntity)
                    .collect(Collectors.toList());

            log.info("✅ Возвращено {} доступных заказов для зоны {}", result.size(), zoneId);
            return result;

        } catch (Exception e) {
            log.error("❌ Ошибка при получении доступных заказов для зоны {}: {}", zoneId, e.getMessage(), e);
            throw new RuntimeException("Ошибка при получении доступных заказов", e);
        }
    }

    @Override
    public Optional<OrderDto> findOrderById(Long id) {
        log.debug("🔍 Поиск заказа по ID: {}", id);

        return orderRepository.findById(id)
                .map(order -> {
                    log.debug("✅ Заказ с ID {} найден", id);
                    return OrderDto.fromEntity(order);
                });
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("🔍 Проверка существования заказа с ID: {}", id);

        boolean exists = orderRepository.existsById(id);
        log.debug("📊 Заказ с ID {} {}существует", id, exists ? "" : "не ");

        return exists;
    }
}
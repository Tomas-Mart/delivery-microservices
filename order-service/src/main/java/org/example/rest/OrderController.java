package org.example.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDto;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления заказами
 * - Внедрение через интерфейс сервиса
 * - Логирование через Lombok
 * - Обработка ошибок
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Отметить заказ как доставленный
     *
     * @param id             идентификатор заказа
     * @param idempotencyKey ключ идемпотентности
     * @return DTO обновленного заказа
     */
    @PostMapping("/{id}/deliver")
    public ResponseEntity<OrderDto> deliverOrder(
            @PathVariable Long id,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        log.info("📝 Запрос на отметку заказа {} как доставленного", id);

        try {
            OrderDto result = orderService.deliverOrder(id, idempotencyKey);
            log.info("✅ Заказ {} успешно отмечен как доставленный", id);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Заказ с ID {} не найден в deliverOrder()", id);
            return ResponseEntity.notFound().build();

        } catch (IllegalStateException e) {
            log.warn("⚠️ Невозможно отметить заказ {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (Exception e) {
            log.error("❌ Ошибка при обработке заказа {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получить список доступных заказов для зоны
     *
     * @param zoneId идентификатор зоны
     * @return список доступных заказов
     */
    @GetMapping("/available")
    public ResponseEntity<List<OrderDto>> getAvailableOrders(@RequestParam String zoneId) {
        log.info("📝 Запрос доступных заказов для зоны: {}", zoneId);

        try {
            List<OrderDto> orders = orderService.getAvailableOrders(zoneId);
            log.info("✅ Найдено {} заказов для зоны {}", orders.size(), zoneId);
            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            log.error("❌ Ошибка при получении заказов для зоны {}: {}", zoneId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Проверить существование заказа
     *
     * @param id идентификатор заказа
     * @return 200 если существует, 404 если нет
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Void> checkOrderExists(@PathVariable Long id) {
        log.debug("🔍 Проверка существования заказа ID: {}", id);

        boolean exists = orderService.existsById(id);

        if (exists) {
            log.debug("✅ Заказ с ID {} существует", id);
            return ResponseEntity.ok().build();
        } else {
            log.debug("⚠️ Заказ с ID {} не существует", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получить заказ по ID (опционально)
     *
     * @param id идентификатор заказа
     * @return DTO заказа или 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        log.info("🔍 Запрос заказа по ID: {}", id);

        return orderService.findOrderById(id)
                .map(order -> {
                    log.info("✅ Заказ с ID {} найден", id);
                    return ResponseEntity.ok(order);
                })
                .orElseGet(() -> {
                    log.warn("⚠️ Заказ с ID {} не найден в getOrderById()", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
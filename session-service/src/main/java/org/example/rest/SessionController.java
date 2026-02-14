package org.example.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.SessionNotFoundException;
import org.example.rest.dto.SessionDto;
import org.example.rest.dto.StartSessionRequest;
import org.example.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для управления сменами курьеров
 * - Внедрение через интерфейс сервиса
 * - Конструктор для зависимостей
 * - Логирование через Lombok
 * - Обработка ошибок
 */
@Slf4j
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    /**
     * Начать новую смену
     *
     * @param request запрос с ID курьера
     * @return созданная смена
     */
    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionDto startSession(@RequestBody StartSessionRequest request) {
        log.info("📝 Запрос на создание смены для курьера: {}", request.getCourierId());

        try {
            SessionDto result = sessionService.start(request.getCourierId());
            log.info("✅ Смена успешно создана с ID: {}", result.getId());
            return result;
        } catch (Exception e) {
            log.error("❌ Ошибка при создании смены: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Проверить активность смены по ID
     *
     * @param id идентификатор смены
     * @return true если смена активна
     */
    @GetMapping("/{id}/active")
    public ResponseEntity<Boolean> isActive(@PathVariable Long id) {
        log.debug("🔍 Проверка активности смены ID: {}", id);

        try {
            boolean isActive = sessionService.isActive(id);
            log.debug("📊 Результат проверки смены ID {}: {}", id, isActive);
            return ResponseEntity.ok(isActive);
        } catch (SessionNotFoundException e) {
            log.warn("⚠️ Смена с ID {} не найдена", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("❌ Ошибка при проверке смены ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Проверить, активен ли курьер
     *
     * @param courierId идентификатор курьера
     * @return true если у курьера есть активная смена
     */
    @GetMapping("/active/{courierId}")
    public ResponseEntity<Boolean> isCourierActive(@PathVariable String courierId) {
        log.debug("🔍 Проверка активности курьера: {}", courierId);

        try {
            boolean isActive = sessionService.isCourierActive(courierId);
            log.debug("📊 Курьер {} активен: {}", courierId, isActive);
            return ResponseEntity.ok(isActive);
        } catch (Exception e) {
            log.error("❌ Ошибка при проверке курьера {}: {}", courierId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получить смену по ID (основной метод)
     *
     * @param id идентификатор смены
     * @return DTO смены
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSession(@PathVariable Long id) {
        log.info("🔍 Запрос на получение смены по ID: {}", id);

        try {
            SessionDto session = sessionService.getSessionById(id);
            log.info("✅ Смена с ID {} успешно получена", id);
            return ResponseEntity.ok(session);
        } catch (SessionNotFoundException e) {
            log.warn("⚠️ Смена с ID {} не найдена", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("❌ Ошибка при получении смены ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получить смену по ID (опционально - возвращает 404, если не найдена)
     *
     * @param id идентификатор смены
     * @return DTO смены или 404
     */
    @GetMapping("/{id}/optional")
    public ResponseEntity<SessionDto> findSession(@PathVariable Long id) {
        log.debug("🔍 Поиск смены по ID: {} (опционально)", id);

        return sessionService.findSessionById(id)
                .map(session -> {
                    log.debug("✅ Смена найдена: {}", session.getId());
                    return ResponseEntity.ok(session);
                })
                .orElseGet(() -> {
                    log.debug("⚠️ Смена с ID {} не найдена", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
package org.example.exception.advice;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для отправки информации об ошибке клиенту
 */
@Data
public class ErrorResponse {
    private int status;           // HTTP статус код
    private String error;          // Тип ошибки
    private String message;        // Сообщение для клиента
    private String path;           // URL запроса
    private LocalDateTime timestamp; // Время ошибки

    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
package org.example.exception;

/**
 * Исключение, выбрасываемое когда смена не найдена
 */
public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(String message) {
        super(message);
    }

    public SessionNotFoundException(Long id) {
        super("Смена с ID " + id + " не найдена");
    }
}
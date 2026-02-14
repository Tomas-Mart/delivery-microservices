package org.example.exception;

/**
 * Исключение, выбрасываемое когда курьер уже имеет активную смену
 */
public class CourierAlreadyHasActiveSessionException extends RuntimeException {

    public CourierAlreadyHasActiveSessionException(String courierId) {
        super("Курьер " + courierId + " уже имеет активную смену");
    }
}
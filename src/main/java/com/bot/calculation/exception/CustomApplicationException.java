package com.bot.calculation.exception;

/**
 * Кастомная ошибка приложения
 */
public class CustomApplicationException extends RuntimeException {

    public CustomApplicationException() {
        super();
    }

    public CustomApplicationException(String message) {
        super(message);
    }

    public CustomApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomApplicationException(Throwable cause) {
        super(cause);
    }
}

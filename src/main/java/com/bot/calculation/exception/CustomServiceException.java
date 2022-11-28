package com.bot.calculation.exception;

/**
 * Ошибка сервиса
 */
public class CustomServiceException extends CustomApplicationException{

    public CustomServiceException() {
        super();
    }

    public CustomServiceException(String message) {
        super(message);
    }

    public CustomServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomServiceException(Throwable cause) {
        super(cause);
    }
}

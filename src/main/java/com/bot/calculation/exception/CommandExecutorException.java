package com.bot.calculation.exception;

/**
 * Ошибка исполнителя комманд
 */
public class CommandExecutorException  extends IllegalArgumentException{

    public CommandExecutorException() {
        super();
    }

    public CommandExecutorException(String s) {
        super(s);
    }

    public CommandExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandExecutorException(Throwable cause) {
        super(cause);
    }
}

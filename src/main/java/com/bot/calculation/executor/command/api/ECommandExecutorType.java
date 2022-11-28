package com.bot.calculation.executor.command.api;

import java.util.Arrays;

public enum ECommandExecutorType {
    START("start", "комманда старт"),
    HELP("help", "комманда помощь"),
    NONE("none", "без комманды"),
    RESULT("result", "результат"),
    DESCRIPTION("description", "описание"),
    CANCEL("cancel", "отмена"),
    EXCHANGE("exchange", "конвертер"),

    RATES("rates", "курсы валют");

    public final String command;
    public final String description;

    ECommandExecutorType(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public static ECommandExecutorType getTypeByCommand(String command){
        String commandSaved = command.toLowerCase();
        return Arrays
                .stream(values())
                .filter(e -> e.command.equals(commandSaved))
                .findFirst()
                .orElse(null);
    }
}

package com.bot.calculation.executor.api;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface ICallbackExecutor {
    void execute(AbsSender absSender, CallbackQuery query);
}

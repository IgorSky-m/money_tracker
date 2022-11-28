package com.bot.calculation.executor.api;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface INonCommandExecutor {
    void execute(AbsSender absSender, Message message);
}

package com.bot.calculation.handler.api;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface INonCommandHandler {
    void handle(AbsSender absSender, Update update);
}

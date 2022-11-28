package com.bot.calculation.telegram;

import com.bot.calculation.executor.command.api.ICommandExecutorFactory;
import com.bot.calculation.handler.api.INonCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * бот
 */
public final class Bot extends TelegramLongPollingCommandBot {
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    private final INonCommandHandler nonCommandHandler;

    public Bot(String token, String name, ICommandExecutorFactory commandExecutorFactory, INonCommandHandler nonCommandHandler) {
        super();
        this.BOT_NAME = name;
        this.BOT_TOKEN = token;
        this.nonCommandHandler = nonCommandHandler;
        registerAll(commandExecutorFactory.getAll().toArray(new IBotCommand[0]));
        logger.info("Бот создан!");
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    /**
     * Ответ на запрос, не являющийся командой
     */
    @Override
    public void processNonCommandUpdate(Update update) {
        this.nonCommandHandler.handle(this, update);
    }


}
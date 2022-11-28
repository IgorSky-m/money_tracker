package com.bot.calculation.executor.command.api;

import org.slf4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Суперкласс для команд создания заданий с различными операциями
 */
public abstract class AIdentifiableCommandExecutor extends BotCommand implements IIdentifiableCommandExecutor {

    private final ECommandExecutorType commandType;

    protected AIdentifiableCommandExecutor(ECommandExecutorType commandType) {
        super(commandType.command, commandType.description);
        this.commandType = commandType;
    }

    protected void sendAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String text) {

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            getLogger().error(String.format("Ошибка %s. Команда %s. Пользователь: %s", e.getMessage(), commandName, userName));
            e.printStackTrace();
        }
    }


    protected void sendRetryAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String text, Integer messageId) {

        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyToMessageId(messageId);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            getLogger().error(String.format("Ошибка %s. Команда %s. Пользователь: %s", e.getMessage(), commandName, userName));
            e.printStackTrace();
        }
    }

    /**
     * Отправка пользователю сообщения об ошибке
     */
    protected void sendError(AbsSender absSender, Long chatId, String commandName, String userName) {
        try {
            absSender.execute(new SendMessage(chatId.toString(), "Похоже, я сломался. Попробуйте позже"));
        } catch (TelegramApiException e) {
            getLogger().error(String.format("Ошибка %s. Команда %s. Пользователь: %s", e.getMessage(), commandName, userName));
            e.printStackTrace();
        }
    }

    public ECommandExecutorType getIdentifier() {
        return commandType;
    }

    protected abstract Logger getLogger();


}
package com.bot.calculation.sender.api;

import com.bot.calculation.sender.api.IMessageSender;
import org.slf4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public abstract class AMessageSender implements IMessageSender {

    private final MessageSource messageSource;

    public AMessageSender(MessageSource messageSource){
        this.messageSource = messageSource;
    };

    @Override
    public void sendAnswer(AbsSender absSender, String chatId, String answer) {
        sendAnswer(absSender, chatId, answer, null);
    }

    @Override
    public void sendAnswer(AbsSender absSender, String chatId, String answer, ReplyKeyboard keyboard) {
        SendMessage msg = SendMessage.builder()
                .text(answer)
                .chatId(chatId)
                .replyMarkup(keyboard)
                .build();

        try {
            absSender.execute(msg);
        } catch (TelegramApiException e) {
           getLogger().error(e.getMessage(), e);
           String errMsg = messageSource.getMessage("error.message.sender.send", null, LocaleContextHolder.getLocale());
           throw new RuntimeException(errMsg, e);
        }
    }

    @Override
    public void sendRetryAnswer(AbsSender absSender,String chatId, Integer retryMessageId, String answer) {
        sendRetryAnswer(absSender, chatId, retryMessageId, answer, null);
    }

    @Override
    public void sendRetryAnswer(AbsSender absSender, String chatId, Integer retryMessageId, String answer, ReplyKeyboard keyboard) {
        SendMessage msg = SendMessage.builder()
                .text(answer)
                .chatId(chatId)
                .replyToMessageId(retryMessageId)
                .replyMarkup(keyboard)
                .build();

        try {
            absSender.execute(msg);
        } catch (TelegramApiException e) {
            getLogger().error(e.getMessage(), e);
            String errMsg = messageSource.getMessage("error.message.sender.send", null, LocaleContextHolder.getLocale());
            throw new RuntimeException(errMsg, e);
        }
    }

    protected abstract Logger getLogger();
}

package com.bot.calculation.sender.api;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface IMessageSender {
    void sendAnswer(AbsSender absSender,String chatId, String answer);
    void sendAnswer(AbsSender absSender,String chatId, String answer, ReplyKeyboard keyboard);
    void sendRetryAnswer(AbsSender absSender, String chatId, Integer retryMessageId, String answer);
    void sendRetryAnswer(AbsSender absSender, String chatId, Integer retryMessageId, String answer, ReplyKeyboard keyboard);



}

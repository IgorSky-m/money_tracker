package com.bot.calculation.handler;

import com.bot.calculation.executor.api.ENonCommandExecutorType;
import com.bot.calculation.executor.api.ICallbackExecutor;
import com.bot.calculation.executor.api.INonCommandExecutor;
import com.bot.calculation.executor.api.INonCommandExecutorFactory;
import com.bot.calculation.executor.command.api.ECommandExecutorType;
import com.bot.calculation.executor.command.api.ICommandExecutorFactory;
import com.bot.calculation.handler.api.INonCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

@Component
public class NonCommandHandler implements INonCommandHandler {

    private final INonCommandExecutorFactory nonCommandExecutorFactory;
    private final ICommandExecutorFactory commandExecutorFactory;
    private final ICallbackExecutor callbackExecutor;

    public NonCommandHandler(
            INonCommandExecutorFactory nonCommandExecutorFactory,
            ICommandExecutorFactory commandExecutorFactory,
            ICallbackExecutor callbackExecutor
    ){
        this.nonCommandExecutorFactory = nonCommandExecutorFactory;
        this.commandExecutorFactory = commandExecutorFactory;
        this.callbackExecutor = callbackExecutor;
    };

    @Override
    public void handle(AbsSender absSender, Update update) {

        Message message = update.getMessage();
        if (message != null){
            handleMessage(absSender, message);
        }
        handleCallback(absSender, update.getCallbackQuery());
    }

    private void handleCallback(AbsSender absSender, CallbackQuery callbackQuery) {
        callbackExecutor.execute(absSender, callbackQuery);
    }

    private void handleMessage(AbsSender absSender, Message message) {

        //Если нет текста - выходим
        // TODO exception?
        if (!message.hasText()){
            return;
        }

        //Если это не ответ на сообщение - предполагаем, что это сообщение сумма
         if (!message.isReply()) {
            INonCommandExecutor executor = nonCommandExecutorFactory.findExecutor(ENonCommandExecutorType.AMOUNT);
            executor.execute(absSender, message);
            return;
        }

        Message replyToMessage = message.getReplyToMessage();

        if (replyToMessage != null){
            if(hasPayload(replyToMessage.getText())){
                String[] split = replyToMessage.getText().split("\\$");
                if (split.length == 0) {
                    deleteMsg(absSender, message.getChatId().toString(), message.getMessageId());
                }
                String payload = split[1];
                String[] payloadArr = payload.split(":");
                if (payloadArr.length == 0) {
                    deleteMsg(absSender, message.getChatId().toString(), message.getMessageId());
                }


                Payload result = decodePayload(payloadArr[1]);

                if (result != null) {
                    commandExecutorFactory
                            .findExecutor(result.getCommand())
                            .execute(absSender, message.getFrom(), replyToMessage.getChat(), new String[]{result.payload, message.getText()});
                } else {
                    deleteMsg(absSender, message.getChatId().toString(), message.getMessageId());
                }
            } else {
                deleteMsg(absSender, message.getChatId().toString(), message.getMessageId());
            }

        }


        //nonCommandExecutor.execute(this, message);
    }


    private boolean hasPayload (String text){
        if (text == null || "".equals(text)){
            return false;
        }
        return text.contains("$payload:");
    }


    private Payload decodePayload(String text){
        String decoded = new String(Base64.getDecoder().decode(text));
        String[] split = decoded.split("\\$");
        if (split.length == 0 || split.length > 2) {
            //todo parse payload err
            return null;
        }
        ECommandExecutorType command = Arrays.stream(split).map(ECommandExecutorType::getTypeByCommand)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (command == null ){
            //todo exception
            return null;
        }

        return new Payload(command, split[1]);
    }



    private static class Payload{
        private final ECommandExecutorType command;
        private final String payload;

        public Payload(ECommandExecutorType command, String payload){
            this.command = command;
            this.payload = payload;
        }

        public ECommandExecutorType getCommand() {
            return command;
        }

        public String getPayload() {
            return payload;
        }
    }


    private void deleteMsg(AbsSender absSender, String chatId, Integer messageId){
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
        try {
            absSender.execute(deleteMessage);
        } catch (TelegramApiException e) {
            //todo обработать исключение
            e.printStackTrace();
        }
    }


}

package com.bot.calculation.executor;

import com.bot.calculation.executor.api.ENonCommandExecutorType;
import com.bot.calculation.executor.api.ICallbackExecutor;
import com.bot.calculation.executor.api.INonCommandExecutorFactory;
import com.bot.calculation.executor.command.api.ECommandExecutorType;
import com.bot.calculation.executor.command.api.ICommandExecutor;
import com.bot.calculation.executor.command.api.ICommandExecutorFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;

@Component
public class CallbackExecutor implements ICallbackExecutor {

    private final ICommandExecutorFactory commandExecutorFactory;
    private final INonCommandExecutorFactory nonCommandExecutorFactory;

    public CallbackExecutor(ICommandExecutorFactory commandExecutorFactory, INonCommandExecutorFactory nonCommandExecutorFactory){
        this.commandExecutorFactory = commandExecutorFactory;
        this.nonCommandExecutorFactory = nonCommandExecutorFactory;
    }


    @Override
    public void execute(AbsSender absSender, CallbackQuery query) {
        if (query == null) {
            return;
        }
        String data = query.getData();
        if (data == null) {
            return;
        }

        String[] arr = data.split(" ");

        if (arr.length == 0) {
            return;
        }

        boolean isCommand = arr[0] != null && arr[0].startsWith("/");

        if (isCommand) {
            String command = arr[0].replace("/", "");
            ECommandExecutorType type = ECommandExecutorType.getTypeByCommand(command);
            if (type == null){
               //TODO exception
                return;
            }

            ICommandExecutor executor = commandExecutorFactory.findExecutor(type);
            if (executor != null) {
                String[] args = arr.length > 1 ? Arrays.copyOfRange(arr, 1, arr.length) : new String[0];
                executor.execute(absSender, query.getFrom(), query.getMessage().getChat(), args);
                return;
            }

            //пока нет других
            //TODO продумать логику получение нужного executor из Factory
            nonCommandExecutorFactory
                    .findExecutor(ENonCommandExecutorType.AMOUNT)
                    .execute(absSender, query.getMessage());
        }
    }
}

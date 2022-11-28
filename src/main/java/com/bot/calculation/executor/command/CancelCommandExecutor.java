package com.bot.calculation.executor.command;

import com.bot.calculation.entity.amount.Amount;
import com.bot.calculation.exception.CommandExecutorException;
import com.bot.calculation.executor.command.api.AIdentifiableCommandExecutor;
import com.bot.calculation.executor.command.api.ECommandExecutorType;
import com.bot.calculation.executor.command.api.ICommandExecutor;
import com.bot.calculation.entity.amount.api.IAmountService;
import com.bot.calculation.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.UUID;

@Service
public class CancelCommandExecutor extends AIdentifiableCommandExecutor implements ICommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CancelCommandExecutor.class);

    private final IAmountService amountService;

    protected CancelCommandExecutor(IAmountService amountService) {
        super(ECommandExecutorType.CANCEL);
        this.amountService = amountService;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String userName = Utils.getUserName(user);
        String answer;
        try {
            UUID uuid = getUuidFromArgs(arguments);
            Amount deleted = amountService.getAmountById(uuid);
            amountService.delete(uuid);
            answer = "запись удалена:\n" + deleted.toString();
        } catch (Exception e) {
            answer = "не удалось найти запись";
        }
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                answer);
    }

    private UUID getUuidFromArgs(String[] arguments) {
        try {
            if (arguments.length == 0) {
                throw new IllegalArgumentException();
            }
            return UUID.fromString(arguments[0]);
        } catch (Exception e) {
            throw new CommandExecutorException(e.getMessage());
        }
    }
}

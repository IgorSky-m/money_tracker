package com.bot.calculation.executor.command;

import com.bot.calculation.entity.amount.Amount;
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class DescriptionCommandExecutor extends AIdentifiableCommandExecutor implements ICommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(DescriptionCommandExecutor.class);
    private static final String REPLY_MSG_TEMPLATE_CREATE = "Отправь ответ на это сообщение, чтобы добавить описание к сумме %s.\n$payload:%s";
    private static final String REPLY_MSG_TEMPLATE_DESCRIPTION_ADD = "комментарий '%s' добавлен.\nid:%s";
    private static final String REPLY_MSG_TEMPLATE_DESCRIPTION_UPDATE = "комментарий '%s' обновлен.\nпредыдущий комментарий: '%s'.\nid:%s";
    private final IAmountService amountService;

    protected DescriptionCommandExecutor(IAmountService amountService) {
        super(ECommandExecutorType.DESCRIPTION);
        this.amountService = amountService;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (arguments == null || arguments.length == 0) {
            //todo exception?
            return;
        }
        String[] args = arguments[0].split(":");
        ARGSCommand command = ARGSCommand.valueOf(args[0]);
        switch (command) {
            case create: {
                executeCreate(absSender, user, chat, UUID.fromString(args[1]));
                break;
            }
            case add: {
                executeAdd(absSender, user, chat, Long.parseLong(args[1]), UUID.fromString(args[2]), arguments[1]);
                break;
            }
            
        }
    }

    private void executeAdd(AbsSender absSender, User user, Chat chat, Long userId, UUID amount, String description) {

        Amount readAmount;
        try {
            readAmount = amountService.getAmountById(amount);
        } catch (Exception e) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), Utils.getUserName(user),
                    "не удалось найти запись");
            return;
        }


        String oldDescription = readAmount.getDescription();
        readAmount.setDescription(description);
        amountService.update(readAmount);

        String answer = buildAddDescriptionAnswer(readAmount, description, oldDescription);
        sendAnswer(absSender, chat.getId(), getCommandIdentifier(), Utils.getUserName(user), answer);

    }
    
    private void executeCreate(AbsSender absSender, User user, Chat chat, UUID uuid){
        String userName = Utils.getUserName(user);
        String answer;
        try {
            Amount amountById = amountService.getAmountById(uuid);
            String payload = ECommandExecutorType.DESCRIPTION.name() + "$" + "add:"  + user.getId() + ":" + amountById.getUuid();
            byte[] encode = Base64.getEncoder().encode(payload.getBytes(StandardCharsets.UTF_8));
            String encoded =  new String(encode);
            answer = String.format(REPLY_MSG_TEMPLATE_CREATE, amountById.getAmount(), encoded);
        } catch (Exception e) {
            answer = "не удалось найти запись";
        }
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                answer);
    };

    private String buildAddDescriptionAnswer(Amount readAmount, String newDescription, String oldDescription){
        if (oldDescription == null || "".equals(oldDescription)) {
            return String.format(REPLY_MSG_TEMPLATE_DESCRIPTION_ADD, newDescription, readAmount.getUuid());
        }
        return String.format(REPLY_MSG_TEMPLATE_DESCRIPTION_UPDATE, newDescription, oldDescription, readAmount.getUuid());
    }
    
    private enum ARGSCommand{
        create,
        add
    }
}

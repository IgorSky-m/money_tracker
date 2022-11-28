package com.bot.calculation.executor.command;

import com.bot.calculation.entity.security.api.ISecurityService;
import com.bot.calculation.parser.api.EUserInfoDictionary;
import com.bot.calculation.util.Utils;
import com.bot.calculation.executor.command.api.AIdentifiableCommandExecutor;
import com.bot.calculation.executor.command.api.ECommandExecutorType;
import com.bot.calculation.executor.command.api.ICommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;

@Service
public class StartCommandExecutor extends AIdentifiableCommandExecutor implements ICommandExecutor {
    private final Logger logger = LoggerFactory.getLogger(StartCommandExecutor.class);

    private final String authParamName;
    private final ISecurityService securityService;

    public StartCommandExecutor(
            @Value("${app.security.auth.name}") String authParamName,
            ISecurityService securityService
    ) {
        super(ECommandExecutorType.START);
        this.authParamName = authParamName;
        this.securityService = securityService;
    }


    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String userName = Utils.getUserName(user);
        String responseText = "Давайте начнём! Если Вам нужна помощь, нажмите /help";
        if (arguments != null && arguments.length > 0) {
            boolean secureCode = Arrays.asList(arguments).contains(authParamName);
            if (secureCode) {
                EUserInfoDictionary byId = EUserInfoDictionary.findById(user.getId());
                responseText = byId == null ?  "Я вас не звал, идите нахуй " : securityService.generatePinCode(user.getId()) ;
            }
        }
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                responseText);
    }


    @Override
    protected Logger getLogger() {
        return logger;
    }

}

package com.bot.calculation.executor.command;

import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.exchanger.api.IExchanger;
import com.bot.calculation.executor.command.api.AIdentifiableCommandExecutor;
import com.bot.calculation.executor.command.api.ECommandExecutorType;
import com.bot.calculation.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.math.BigDecimal;

/**
 * конвертер валют
 * конвертировать сумму в валюте 'from' в сумму в валюте 'to'
 * /exchange 'сумма' 'from' 'to'
 * /exchange 100 usd gel
 */
@Service
public class ExchangeCommandExecutor extends AIdentifiableCommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeCommandExecutor.class);
    private final IExchanger exchanger;

    protected ExchangeCommandExecutor(IExchanger exchanger) {
        super(ECommandExecutorType.EXCHANGE);
        this.exchanger = exchanger;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        ExchangeArgs exchangeArgs = parseArgs(arguments);
        String answer;
        if (isValid(exchangeArgs)){
            answer = getValidExchangeText(exchangeArgs);
        } else {
            answer = getInvalidCommandAnswer();
        }

        sendAnswer(
                absSender,
                chat.getId(),
                this.getCommandIdentifier(),
                Utils.getUserName(user),
                answer
        );
    }

    private String getValidExchangeText(ExchangeArgs exchangeArgs) {
        return exchangeArgs.value +
                " " +
                exchangeArgs.from.name() +
                " = " +
                exchanger.exchange(exchangeArgs.from, exchangeArgs.to, exchangeArgs.value) +
                " " +
                exchangeArgs.to.name();
    }


    private String getInvalidCommandAnswer() {
        return "не хватает аргументов.\nПопробуй ввести:\n/exchange <сумма> <валюта суммы> <валюта, в которую конвертируешь>";
    }


    private ExchangeArgs parseArgs(String[] args){
        if (args == null || args.length < 3) {
            return null;
        }
        ExchangeArgs exchangeArgs = new ExchangeArgs();
        exchangeArgs.value = BigDecimal.valueOf(Double.parseDouble(args[0]));
        exchangeArgs.from = ECurrencyCode.safeFindCurrencyByValueIgnoreCase(args[1]);
        exchangeArgs.to = ECurrencyCode.safeFindCurrencyByValueIgnoreCase(args[2]);

        return exchangeArgs;
    }

    private boolean isValid(ExchangeArgs args){
        return args != null &&
                args.value != null &&
                args.from != null &&
                args.to != null;
    }

    private static class ExchangeArgs{
        private BigDecimal value;
        private ECurrencyCode from;
        private ECurrencyCode to;
    }
}

package com.bot.calculation.executor.command;

import com.bot.calculation.entity.currency.ExchangeCurrency;
import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.entity.currency.api.ICurrencyService;
import com.bot.calculation.entity.currency.utils.CurrencyUtils;
import com.bot.calculation.executor.command.api.AIdentifiableCommandExecutor;
import com.bot.calculation.executor.command.api.ECommandExecutorType;
import com.bot.calculation.executor.command.api.ICommandExecutor;
import com.bot.calculation.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.text.SimpleDateFormat;

/**
 * /rates - получить текущие курсы
 */
@Service
public class RatesCommandExecutor extends AIdentifiableCommandExecutor implements ICommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(RatesCommandExecutor.class);
    private static final String SIMPLE_DATE_FORMAT_PATTERN = "dd MMMMM yyyy";
    private final ICurrencyService currencyService;
    private final SimpleDateFormat simpleDateFormat;

    protected RatesCommandExecutor(ICurrencyService currencyService) {
        super(ECommandExecutorType.RATES);
        this.currencyService = currencyService;
        this.simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT_PATTERN);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        sendAnswer(
                absSender,
                chat.getId(),
                this.getCommandIdentifier(),
                Utils.getUserName(user),
                getRatesText(currencyService.getLastCurrency())
        );

    }

    private String getRatesText(ExchangeCurrency lastCurrency) {
        if (lastCurrency == null) {
            return "курсы не найдены";
        }

        StringBuilder builder = new StringBuilder("Курсы для gel на ")
                .append(simpleDateFormat.format(lastCurrency.getDtCreate()))
                .append(":\n");
        for (ECurrencyCode value : ECurrencyCode.values()) {
            builder.append(value.name())
                    .append(": ")
                    .append(CurrencyUtils.getCurrencyByCode(value, lastCurrency))
                    .append("\n");
        }

        return builder.toString();
    }
}

package com.bot.calculation.executor;

import com.bot.calculation.entity.amount.Amount;
import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.entity.currency.api.ICurrencyService;
import com.bot.calculation.exception.CommandExecutorException;
import com.bot.calculation.exception.CustomApplicationException;
import com.bot.calculation.exception.ParserException;
import com.bot.calculation.exchanger.api.IExchanger;
import com.bot.calculation.executor.api.AIdentifiableNonCommandExecutor;
import com.bot.calculation.executor.api.ENonCommandExecutorType;
import com.bot.calculation.parser.AmountParser;
import com.bot.calculation.parser.api.IParserResponse;
import com.bot.calculation.parser.api.ITextParser;
import com.bot.calculation.parser.api.EUserInfoDictionary;
import com.bot.calculation.sender.api.IMessageSender;
import com.bot.calculation.entity.amount.api.IAmountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Обработка сообщения, не являющегося командой (т.е. обычного текста не начинающегося с "/")
 */
@Service
public class AmountNonCommandExecutor extends AIdentifiableNonCommandExecutor {

    private static final String DESCRIPTION_CALLBACK_PATTERN = "/description create:%s";
    private static final String CANCEL_CALLBACK_PATTERN = "/cancel %s";
    private static final String GENERAL_EXCEPTION_TEXT = "не понимаю тебя нажми /help";
    private static final Logger logger = LoggerFactory.getLogger(AmountNonCommandExecutor.class);
    private static final String ADD = "Добавить";
    private static final String UPDATE = "Обновить";
    private static final String SIMPLE_DATE_FORMAT_PATTERN = "EEEEE dd MMMMM yyyy";

    private final ITextParser amountParser;
    private final IAmountService amountService;
    private final ICurrencyService currencyService;
    private final IExchanger exchanger;
    private final SimpleDateFormat simpleDateFormat;


    public AmountNonCommandExecutor(
            @Qualifier(AmountParser.PARSER_NAME) ITextParser amountParser,
            IMessageSender messageSender,
            IAmountService amountService,
            ICurrencyService currencyService,
            IExchanger exchanger
    ){
        super(ENonCommandExecutorType.AMOUNT, messageSender);
        this.amountParser = amountParser;
        this.amountService = amountService;
        this.currencyService = currencyService;
        this.exchanger = exchanger;
        //todo локаль не будет совпадать? проверить
        this.simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT_PATTERN);
    }

    @Override
    public void execute(AbsSender absSender, Message message) {
        String answer;
        ReplyKeyboard keyboard = null;

        try {
            IParserResponse response = amountParser.parse(message.getText());
            if (response.getUser() == null) {
                EUserInfoDictionary user = EUserInfoDictionary.findById(message.getFrom().getId());
                response.setUser(user);
                if (user == null) {
                    throw new CommandExecutorException("не могу найти юзера");
                }
            }

            BigDecimal resultAmount = response.getAmount();
            if (response.getCurrency() != null) {
                resultAmount = exchanger.exchange(response.getCurrency(), ECurrencyCode.GEL, response.getAmount());
            }

            Amount amount = new Amount();
            amount.setUuid(UUID.randomUUID());
            amount.setDtCreate(new Date());
            amount.setAmount(resultAmount);
            amount.setUserId(response.getUser().id);
            amount.setDescription(response.getDescription());
            amount.setCurrency(currencyService.getLastCurrency());
            amountService.save(amount);


            StringBuilder result = new StringBuilder("Принял.\n")
                    .append("Дата: ").append(simpleDateFormat.format(amount.getDtCreate())).append("\n")
                    .append("Пользователь: ").append(response.getUser().username).append("\n")
                    .append("Сумма: ").append(amount.getAmount().toString()).append(" GEL\n");
            if (amount.getDescription() != null) {
                result.append("Описание: ").append(amount.getDescription()).append("\n");
            }
            String descriptionButton = amount.getDescription() == null ? ADD : UPDATE;
                    result.append("Нажми кнопку '").append(descriptionButton)
                            .append(" описание', если хочешь ").append(descriptionButton.toLowerCase())
                            .append(" описание\n")
                            .append("Нажми 'Отменить' чтобы отменить запись");

            answer = result.toString();

            List<InlineKeyboardButton> buttons = new ArrayList<>();
                buttons.add(InlineKeyboardButton.builder()
                        .text(descriptionButton + " описание")
                        .callbackData(String.format(DESCRIPTION_CALLBACK_PATTERN, amount.getUuid()))
                        .build());
                buttons.add(InlineKeyboardButton.builder()
                        .text("Отменить")
                        .callbackData(String.format(CANCEL_CALLBACK_PATTERN, amount.getUuid()))
                        .build());

            keyboard = InlineKeyboardMarkup.builder()
                    .keyboardRow(buttons).build();

        } catch (ParserException | CommandExecutorException | CustomApplicationException e) {
            answer = e.getMessage();
        } catch (Exception e) {
            answer = GENERAL_EXCEPTION_TEXT;
        }

        messageSender.sendRetryAnswer(absSender,message.getChatId().toString(), message.getMessageId(), answer, keyboard);
    }


    @Override
    protected Logger getLogger() {
        return logger;
    }
}
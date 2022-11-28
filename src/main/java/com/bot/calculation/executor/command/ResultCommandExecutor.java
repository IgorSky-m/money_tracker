package com.bot.calculation.executor.command;

import com.bot.calculation.entity.amount.Amount;
import com.bot.calculation.entity.amount.api.IAmountService;
import com.bot.calculation.executor.command.api.AIdentifiableCommandExecutor;
import com.bot.calculation.executor.command.api.ECommandExecutorType;
import com.bot.calculation.executor.command.api.ICommandExecutor;
import com.bot.calculation.parser.api.EUserInfoDictionary;
import com.bot.calculation.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

@Service
public class ResultCommandExecutor extends AIdentifiableCommandExecutor implements ICommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ResultCommandExecutor.class);
    private static final String PARSE_ARGS_ERR = "не смог распарсить аргументы";
    private final IAmountService amountService;

    protected ResultCommandExecutor(IAmountService amountService) {
        super(ECommandExecutorType.RESULT);
        this.amountService = amountService;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String answer;
        YearMonth yearMonth = null;
        try {
            if (arguments.length > 0) {

                yearMonth = parseYearMonth(arguments[0]);
            }

            List<Amount> result;

            if (yearMonth == null) {
                result = amountService.getAll();
            } else {
                result = amountService.getByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue());
            }

            answer = createAnswer(result);

        } catch (IllegalArgumentException e) {
            answer = e.getMessage();
        } catch (Exception e){
            answer = "не понял тебя";
        }
        sendAnswer(absSender, chat.getId(), getCommandIdentifier(), Utils.getUserName(user), answer);
    }

    private String createAnswer(List<Amount> result) {
        Map<Long, BigDecimal> resultByUser = calculateResult(result);
        if (resultByUser.isEmpty()) {
            return "записей не найдено";
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Long, BigDecimal> entry : resultByUser.entrySet()) {
            EUserInfoDictionary userInfo = EUserInfoDictionary.findById(entry.getKey());
            String user = userInfo != null ? userInfo.username : entry.getKey().toString();
            builder.append(user)
                    .append(": ")
                    .append(entry.getValue())
                    .append(" GEL\n");
        }

        return builder.toString();

    }


    private Map<Long, BigDecimal> calculateResult(List<Amount> amounts){
        Map<Long, BigDecimal> result = new HashMap<>();
        if (amounts == null || amounts.isEmpty()){
            return Collections.emptyMap();
        }
        for (Amount amount : amounts) {
            if (result.containsKey(amount.getUserId())){
                BigDecimal bigDecimal = result.get(amount.getUserId()).add(amount.getAmount());
                result.put(amount.getUserId(), bigDecimal);
                continue;
            }
            result.put(amount.getUserId(),amount.getAmount());
        }

        return result;
    }


    private YearMonth parseYearMonth(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }

        String[] split = str.split("\\.");
        int year;
        int month;
        if (split.length == 0 || split.length > 2) {
            throw new IllegalArgumentException(PARSE_ARGS_ERR);
        }
        if (split.length == 1) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            try {
                month = Integer.parseInt(split[0]);
            } catch (Exception e) {
                throw new IllegalArgumentException(PARSE_ARGS_ERR);
            }
        } else {
            month = Integer.parseInt(split[0]);
            String yearStr;
            if (split[1].length() == 2) {
                yearStr = "20" + split[1];
            } else {
               yearStr = split[1];
            }

            try {
                year = Integer.parseInt(yearStr);
            } catch (Exception e) {
                throw new IllegalArgumentException(PARSE_ARGS_ERR);
            }
        }

        return YearMonth.of(year, month);
    }
}

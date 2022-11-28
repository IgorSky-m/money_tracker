package com.bot.calculation.util;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class Utils {

    /**
     * Формирование имени пользователя
     * @param msg сообщение
     */
    public static String getUserName(Message msg) {
        return getUserName(msg.getFrom());
    }

    /**
     * Формирование имени пользователя. Если заполнен никнейм, используем его. Если нет - используем фамилию и имя
     * @param user пользователь
     */
    public static String getUserName(User user) {
        return (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
    }

}

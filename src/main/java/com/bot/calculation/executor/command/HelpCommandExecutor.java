package com.bot.calculation.executor.command;

import com.bot.calculation.entity.currency.api.ECurrencyCode;
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

@Service
public class HelpCommandExecutor extends AIdentifiableCommandExecutor implements ICommandExecutor {
    private final Logger logger = LoggerFactory.getLogger(HelpCommandExecutor.class);

    public HelpCommandExecutor() {
        super(ECommandExecutorType.HELP);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String userName = Utils.getUserName(user);
        sendAnswer(
                absSender,
                chat.getId(),
                this.getCommandIdentifier(),
                userName,
                getHelpText()
        );
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    private String getHelpText(){

        StringBuilder builder = new StringBuilder();
        builder.append("Для записи данных принимаю формат чисел целых чисел и дробных через '.'\n")
                .append("пример: 123.0\n")
                .append("По умолчаюнию данные закрепляются за отправителем сообщения.\n")
                .append("Если хочешь указать иного человека, кому записать данные, укажи букву или имя на ru или en.\n")
                .append("примеры:\n123.0 i\n45 максим\n")
                .append("Так же можно указать валюту, в которой ты хочешь записать сумму. по умолчанию это ")
                .append(ECurrencyCode.GEL.getCurrencyValueLowerCase()).append("\n")
                .append("Доступные валюты:\n");

        ECurrencyCode[] currencies = ECurrencyCode.values();
        for (int i = 0; i < currencies.length; i++) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(currencies[i].getCurrencyValueLowerCase());
        }

        builder.append("\n")
                .append("Пример: 123 usd m - сконвертирует 123 usd в gel и запишет пользователю 'm'\n")
                .append("Если я не смогу отнести аргументы к какой-либо категории, то запишу их в описание к записи\n")
                .append("Пример: 123 abc - запишет 123 GEL с описанием 'abc' пользователю, отправившему сообщение\n")
                .append("Чтобы добавить или обновить описание к данным позднее, нажми соответствующую кнопку бота под ответным сообщением.\n\n")
                .append("Чтобы посмотреть результат воспользуйтся коммандой /result\n")
                .append("Чтобы фильтровать по месяцу (с начала по конец) ты можешь указать номер месяца\n")
                .append("пример: /result 04 или /result 4 - отдаст результат за апрель текущего года\n")
                .append("Так же ты можешь указать год через '.'\n")
                .append("пример: /result 04.22 или /result 4.2022 - отдаст записи за апрель 2022 года");

        return builder.toString();
    }

}

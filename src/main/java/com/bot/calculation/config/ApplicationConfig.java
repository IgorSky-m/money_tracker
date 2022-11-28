package com.bot.calculation.config;

import com.bot.calculation.executor.command.api.ICommandExecutorFactory;
import com.bot.calculation.handler.api.INonCommandHandler;
import com.bot.calculation.telegram.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Конфигурация приложения
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(
            @Value("${com.bot.calculation.settings.TOKEN}") String token,
            @Value("${com.bot.calculation.settings.NAME}") String name,
            ICommandExecutorFactory commandExecutorFactory,
            INonCommandHandler nonCommandHandler
            ) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new Bot(token, name, commandExecutorFactory, nonCommandHandler));
        return botsApi;
    }


}

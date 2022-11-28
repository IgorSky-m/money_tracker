package com.bot.calculation.job.com.currency;

import com.bot.calculation.entity.currency.ExchangeCurrency;
import com.bot.calculation.entity.currency.api.ICurrencyService;
import com.bot.calculation.transport.com.currency.CurrencyComExchange;
import com.bot.calculation.transport.com.currency.api.ECurrencyComExchangePair;
import com.bot.calculation.transport.com.currency.api.ICurrencyComExchangeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Executor;

@Service
@ConditionalOnProperty(value = "com.bot.calculation.job.crypto.executors.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class GetCurrencyComProcessor {

    private static final String EXECUTOR_NAME = "getCurrencyCryptoProcessorExecutor";

    private final ICurrencyComExchangeClient currencyComExchangeClient;
    private final ICurrencyService currencyService;

    public GetCurrencyComProcessor(
            ICurrencyComExchangeClient currencyComExchangeClient,
            ICurrencyService currencyService
    ){
        this.currencyComExchangeClient = currencyComExchangeClient;
        this.currencyService = currencyService;
    }

    @Async(GetCurrencyComProcessor.EXECUTOR_NAME)
    @Scheduled(cron = "${com.bot.calculation.job.crypto.executors.cron}")
    public void process() {
        CurrencyComExchange btcUsd;
        CurrencyComExchange ethUsd;
        try {
            btcUsd = currencyComExchangeClient.getExchange(ECurrencyComExchangePair.BTC_USD);
            ethUsd = currencyComExchangeClient.getExchange(ECurrencyComExchangePair.ETH_USD);
        } catch (Exception e){
            //todo exception
            throw new RuntimeException("не могу получить курсы крипты", e);
        }

        ExchangeCurrency currency = currencyService.getLastCurrency();
        if (currency != null) {
            currency.setBtc(usdToCurrencyConvert(btcUsd.getWeightedAvgPrice()));
            currency.setEth(usdToCurrencyConvert(ethUsd.getWeightedAvgPrice()));
            currencyService.update(currency);
        }
    }

    private BigDecimal usdToCurrencyConvert(Double currencyToUsd) {
        return new BigDecimal(1)
                .divide(new BigDecimal(currencyToUsd),10, RoundingMode.HALF_UP);
    }




    @Configuration
    @EnableAsync
    //https://www.baeldung.com/spring-async#1-override-the-executor-at-the-method-level
    public static class GetCurrencyCryptoProcessorConfiguration {

        @Bean(name = GetCurrencyComProcessor.EXECUTOR_NAME)
        public Executor threadPoolTaskExecutor(@Value("${com.bot.calculation.job.crypto.executors.pool-size}") int poolSize) {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            threadPoolTaskExecutor.setThreadNamePrefix("get-currency-crypto-processor-executor-");
            threadPoolTaskExecutor.setCorePoolSize(poolSize);
            threadPoolTaskExecutor.initialize();
            return threadPoolTaskExecutor;
        }

    }
}

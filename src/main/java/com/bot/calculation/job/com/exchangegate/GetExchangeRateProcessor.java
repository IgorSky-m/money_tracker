package com.bot.calculation.job.com.exchangegate;

import com.bot.calculation.entity.currency.ExchangeCurrency;
import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.entity.currency.api.ICurrencyService;
import com.bot.calculation.transport.com.currency.CurrencyComExchange;
import com.bot.calculation.transport.com.currency.api.ECurrencyComExchangePair;
import com.bot.calculation.transport.com.currency.api.ICurrencyComExchangeClient;
import com.bot.calculation.transport.com.exchangerate.ExchangeRateCurrency;
import com.bot.calculation.transport.com.exchangerate.api.IExchangeCurrencyRateClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

@Service
@ConditionalOnProperty(value = "com.bot.calculation.job.fiat.executors.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class GetExchangeRateProcessor{

    private static final String EXECUTOR_NAME = "getCurrencyFiatProcessorExecutor";

    private final IExchangeCurrencyRateClient exchangeCurrencyRateClient;
    private final ICurrencyComExchangeClient currencyComExchangeClient;
    private final ICurrencyService currencyService;

    public GetExchangeRateProcessor(
            IExchangeCurrencyRateClient exchangeCurrencyRateClient,
            ICurrencyComExchangeClient currencyComExchangeClient,
            ICurrencyService currencyService
    ){
        this.exchangeCurrencyRateClient = exchangeCurrencyRateClient;
        this.currencyComExchangeClient = currencyComExchangeClient;
        this.currencyService = currencyService;
    }

    @Async(GetExchangeRateProcessor.EXECUTOR_NAME)
    @Scheduled(cron = "${com.bot.calculation.job.fiat.executors.cron}")
    public void process() {
        ExchangeRateCurrency exchange;
        try {
            //подумать что делать если не получилил актуальных курсов
            exchange = exchangeCurrencyRateClient.getExchange(ECurrencyCode.GEL);
        } catch (Exception e){
            //todo exception & log
            throw new IllegalArgumentException("не смог получить валюту");
        }

        Map<String, Double> conversionRates = null;

        if (exchange != null) {
            conversionRates = exchange.getConversionRates();
        }

        if (conversionRates == null) {
            //todo exception & log
            throw new IllegalArgumentException("не смог получить валюту");
        }
        boolean createNewInstance = false;
        ExchangeCurrency currency = null;
        ExchangeCurrency lastCurrency = currencyService.getLastCurrency();
        if (lastCurrency != null) {
            ZonedDateTime date = ZonedDateTime.ofInstant(lastCurrency.getDtCreate().toInstant(), ZoneId.systemDefault());
            int dayOfMonth = date.getDayOfMonth();
            ZonedDateTime current = ZonedDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
            if (dayOfMonth != current.getDayOfMonth()) {
                createNewInstance = true;
            } else {
                currency = lastCurrency;
            }
        } else {
            createNewInstance = true;
        }

        if (createNewInstance) {
            currency = new ExchangeCurrency();
            currency.setUuid(UUID.randomUUID());
            currency.setDtCreate(new Date());
        }

        currency.setUsd(BigDecimal.valueOf(conversionRates.get(ECurrencyCode.USD.name())));
        currency.setEur(BigDecimal.valueOf(conversionRates.get(ECurrencyCode.EUR.name())));
        currency.setByn(BigDecimal.valueOf(conversionRates.get(ECurrencyCode.BYN.name())));
        currency.setRub(BigDecimal.valueOf(conversionRates.get(ECurrencyCode.RUB.name())));
        currency.setGel(BigDecimal.valueOf(conversionRates.get(ECurrencyCode.GEL.name())));
        currencyService.create(currency);

        CurrencyComExchange btcExchange;
        CurrencyComExchange ethExchange;

        try {
            //todo convert to lari
            btcExchange = currencyComExchangeClient.getExchange(ECurrencyComExchangePair.BTC_USD);
            ethExchange = currencyComExchangeClient.getExchange(ECurrencyComExchangePair.ETH_USD);
        } catch (Exception e){
            //todo exception & log
            throw new RuntimeException("не смог получить припту\n", e);
        }

        currency.setBtc(usdToCurrencyConvert(btcExchange.getWeightedAvgPrice()));
        currency.setEth(usdToCurrencyConvert(ethExchange.getWeightedAvgPrice()));
        currencyService.update(currency);
    }

    private BigDecimal usdToCurrencyConvert(Double currencyToUsd) {
        return new BigDecimal(1)
                .divide(new BigDecimal(currencyToUsd),10, RoundingMode.HALF_UP);
    }


    @EventListener({ContextRefreshedEvent.class})
    public void postConstruct(){
         process();
    }


    @Configuration
    @EnableAsync
    //https://www.baeldung.com/spring-async#1-override-the-executor-at-the-method-level
    public static class GetCurrencyFiatProcessorConfiguration {

        @Bean(name = GetExchangeRateProcessor.EXECUTOR_NAME)
        public Executor threadPoolTaskExecutor(@Value("${com.bot.calculation.job.fiat.executors.pool-size}") int poolSize) {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            threadPoolTaskExecutor.setThreadNamePrefix("get-currency-fiat-processor-executor-");
            threadPoolTaskExecutor.setCorePoolSize(poolSize);
            threadPoolTaskExecutor.initialize();
            return threadPoolTaskExecutor;
        }

    }
}

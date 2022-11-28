package com.bot.calculation.transport.com.currency;

import com.bot.calculation.entity.currency.ExchangeCurrency;
import com.bot.calculation.transport.com.currency.api.ECurrencyComExchangePair;
import com.bot.calculation.transport.com.currency.api.ICurrencyComExchangeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CurrencyComExchangeClient implements ICurrencyComExchangeClient {

    private final String addPath;
    private final String baseUrl;

    private final String aliveTestPath = "https://calculate-bot-georgia.herokuapp.com/exchange/rates/current";

    private static final String SYMBOL_PARAM_NAME = "symbol";
    private static final String WEIGHT_AVG_PARAM_NAME = "weightedAvgPrice";

    private final String GET_CURRENCY_URL;
    private final String GET_ALL_CURRENCIES_URL;
    private final RestTemplate restTemplate;

    public CurrencyComExchangeClient(
            @Value("${com.bot.transport.com.currency.url}") String baseUrl,
            @Value("${com.bot.transport.com.currency.exchange.path}") String addPath,
            RestTemplateBuilder restTemplateBuilder
    ){
        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
        this.addPath = addPath;

        GET_ALL_CURRENCIES_URL = baseUrl + "/" + addPath;
        GET_CURRENCY_URL = GET_ALL_CURRENCIES_URL + "?" + SYMBOL_PARAM_NAME + "={symbol}";
    }

    @Override
    public CurrencyComExchange getExchange(ECurrencyComExchangePair symbol) {

        ResponseEntity<ExchangeCurrency> alive = restTemplate.getForEntity(aliveTestPath, ExchangeCurrency.class);

        if (HttpStatus.OK.equals(alive.getStatusCode())) {
            System.out.println("----------------------------------------Self request alive status 200------------------------------------");
        }

        ResponseEntity<CurrencyComExchange> response = restTemplate.getForEntity(GET_CURRENCY_URL, CurrencyComExchange.class, symbol.getValue());

        if (HttpStatus.OK.equals(response.getStatusCode())){
            return response.getBody();
        }

        throw new IllegalArgumentException("cant get feign entities");
    }

    @Override
    public List<CurrencyComExchange> getAllExchanges() {
        ResponseEntity<CurrencyComExchange[]> response = restTemplate.getForEntity(GET_ALL_CURRENCIES_URL, CurrencyComExchange[].class);
        if (HttpStatus.OK.equals(response.getStatusCode())){
            if (response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
            return Collections.EMPTY_LIST;
        }

        throw new IllegalArgumentException("cant get feign entities");
    }


}

package com.bot.calculation.transport.com.exchangerate;

import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.transport.com.exchangerate.api.IExchangeCurrencyRateClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateCurrencyRestClient implements IExchangeCurrencyRateClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String token;
    private final String GET_ACTUAL_CURRENCY_REQUEST;

    public ExchangeRateCurrencyRestClient(
            @Value("${com.bot.transport.com.exchange_rate.url}") String baseUrl,
            @Value("${com.bot.transport.com.exchange_rate.token}") String token,
            RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
        this.token = token;

        GET_ACTUAL_CURRENCY_REQUEST = buildGET_ACTUAL_CURRENCY_REQUEST();
    }

    @Override
    public ExchangeRateCurrency getExchange(ECurrencyCode baseCode) {
        ResponseEntity<ExchangeRateCurrency> response = restTemplate.getForEntity(GET_ACTUAL_CURRENCY_REQUEST, ExchangeRateCurrency.class, baseCode.name());
        if (response.getStatusCode().equals(HttpStatus.OK)){
            return response.getBody();
        }

        throw new IllegalArgumentException("cant get response");
    }

    private String buildGET_ACTUAL_CURRENCY_REQUEST() {
        return baseUrl + "/" + token + "/latest/{base_currency}";
    }
}


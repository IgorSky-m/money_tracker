package com.bot.calculation.entity.currency.api;

import java.util.Arrays;

public enum ECurrencyCode {
    USD,
    EUR,
    GEL,
    BYN,
    RUB,
    BTC,
    ETH;



    public String getCurrencyValueLowerCase(){
        return name().toLowerCase();
    }


    public static ECurrencyCode safeFindCurrencyByValueIgnoreCase(String code) {
        final String value = code.toLowerCase();
        return Arrays.stream(values())
                .filter(e -> e.getCurrencyValueLowerCase().equals(value))
                .findFirst()
                .orElse(null);
    }


}

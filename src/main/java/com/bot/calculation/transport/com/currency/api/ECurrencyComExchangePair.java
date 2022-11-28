package com.bot.calculation.transport.com.currency.api;

public enum ECurrencyComExchangePair {
    BTC_USD("BTC/USD"),
    ETH_USD("ETH/USD");

    private final String value;

    ECurrencyComExchangePair(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package com.bot.calculation.transport.com.currency;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Currency.com
 */
public class CurrencyComExchange {
    private String symbol;

    @JsonProperty(value = "weightedAvgPrice")
    private Double weightedAvgPrice;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getWeightedAvgPrice() {
        return weightedAvgPrice;
    }

    public void setWeightedAvgPrice(Double weightedAvgPrice) {
        this.weightedAvgPrice = weightedAvgPrice;
    }


}

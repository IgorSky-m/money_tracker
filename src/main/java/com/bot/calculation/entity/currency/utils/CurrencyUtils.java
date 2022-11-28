package com.bot.calculation.entity.currency.utils;

import com.bot.calculation.entity.currency.ExchangeCurrency;
import com.bot.calculation.entity.currency.api.ECurrencyCode;

import java.math.BigDecimal;

public class CurrencyUtils {

    public static BigDecimal getCurrencyByCode(ECurrencyCode code, ExchangeCurrency currency) {
        if (currency == null) {
            return null;
        }

        switch (code){
            case BTC: return currency.getBtc();
            case BYN: return currency.getByn();
            case ETH: return currency.getEth();
            case EUR: return currency.getEur();
            case GEL: return currency.getGel();
            case RUB: return currency.getRub();
            case USD: return currency.getUsd();
            default: return null;
        }
    }
}

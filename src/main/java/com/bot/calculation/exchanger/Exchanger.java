package com.bot.calculation.exchanger;

import com.bot.calculation.entity.currency.ExchangeCurrency;
import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.entity.currency.api.ICurrencyService;
import com.bot.calculation.entity.currency.utils.CurrencyUtils;
import com.bot.calculation.exchanger.api.IExchanger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class Exchanger implements IExchanger {

    private final ICurrencyService currencyService;

    public Exchanger(ICurrencyService currencyService){
        this.currencyService = currencyService;
    }

    @Override
    public BigDecimal exchange(ECurrencyCode from, ECurrencyCode to, BigDecimal amountFrom) {
        if (from == null || to == null || amountFrom == null) {
            throw new IllegalArgumentException("exchange parameters can't be empty");
        }

        ExchangeCurrency lastCurrency = currencyService.getLastCurrency();


        if (lastCurrency == null) {
            throw new IllegalArgumentException("read currency error");
        }

        BigDecimal fromCourse = CurrencyUtils.getCurrencyByCode(from, lastCurrency);
        BigDecimal toCourse = CurrencyUtils.getCurrencyByCode(to, lastCurrency);

        if (fromCourse == null || toCourse == null) {
            throw new IllegalArgumentException("courses can't be empty");
        }

        return amountFrom.divide(fromCourse,2, RoundingMode.HALF_UP).multiply(toCourse);
    }



    // 100 * 1 / 3.05

    //from//100 GEL - 1
    //to UDS - 3.05

    //100 * 3.05 / 1
    //100 usd - 3.05
    // to gel - 1


    //100 * 3.05 / 3.1

    //100 usd 3.05
    //100 eur 3.1


    //100 BYN - 3.36
    // EUR - 0.94

    //

}

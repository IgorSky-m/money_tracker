package com.bot.calculation.entity.currency.api;

import com.bot.calculation.entity.currency.ExchangeCurrency;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ICurrencyService {
    ExchangeCurrency getLastCurrency();
    ExchangeCurrency getCurrencyByDate(Date dtCreate);

    Optional<ExchangeCurrency> findCurrencyByDate(Date date);

    void create(ExchangeCurrency currency);

    void update(ExchangeCurrency currency);

    List<ExchangeCurrency> getAll();
}

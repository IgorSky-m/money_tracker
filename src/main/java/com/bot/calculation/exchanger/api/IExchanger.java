package com.bot.calculation.exchanger.api;

import com.bot.calculation.entity.currency.api.ECurrencyCode;

import java.math.BigDecimal;

public interface IExchanger {
    BigDecimal exchange(ECurrencyCode from, ECurrencyCode to, BigDecimal amount);
}

package com.bot.calculation.transport.com.exchangerate.api;

import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.transport.com.api.IExchangeClient;
import com.bot.calculation.transport.com.exchangerate.ExchangeRateCurrency;

public interface IExchangeCurrencyRateClient extends IExchangeClient<ECurrencyCode, ExchangeRateCurrency> {

}

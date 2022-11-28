package com.bot.calculation.transport.com.currency.api;

import com.bot.calculation.transport.com.api.IExchangeClient;
import com.bot.calculation.transport.com.currency.CurrencyComExchange;

import java.util.List;

public interface ICurrencyComExchangeClient extends IExchangeClient<ECurrencyComExchangePair, CurrencyComExchange> {
    List<CurrencyComExchange> getAllExchanges();
}

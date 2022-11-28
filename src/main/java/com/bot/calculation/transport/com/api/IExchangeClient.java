package com.bot.calculation.transport.com.api;

public interface IExchangeClient<REQUEST, RESPONSE> {
    RESPONSE getExchange(REQUEST request);
}

package com.bot.calculation.parser.api;

import com.bot.calculation.entity.currency.api.ECurrencyCode;

import java.math.BigDecimal;

public interface IParserResponse {
    EUserInfoDictionary getUser();
    void setUser(EUserInfoDictionary user);
    BigDecimal getAmount();
    void setAmount(BigDecimal amount);
    String getDescription();
    void setDescription(String description);
    ECurrencyCode getCurrency();
    void setCurrency(ECurrencyCode currency);
}

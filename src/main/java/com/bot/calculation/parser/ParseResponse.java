package com.bot.calculation.parser;

import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.parser.api.IParserResponse;
import com.bot.calculation.parser.api.EUserInfoDictionary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ParseResponse implements IParserResponse {

    private EUserInfoDictionary user;
    private BigDecimal amount;
    private String description;
    private ECurrencyCode currency;


}

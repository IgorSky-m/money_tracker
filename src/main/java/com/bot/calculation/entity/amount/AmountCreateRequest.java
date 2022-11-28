package com.bot.calculation.entity.amount;

import com.bot.calculation.entity.currency.api.ECurrencyCode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AmountCreateRequest  {

    private Date date;

    private Long userId;

    private BigDecimal amount;

    private String description;

    private ECurrencyCode currency;

}

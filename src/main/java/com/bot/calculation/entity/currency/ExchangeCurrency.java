package com.bot.calculation.entity.currency;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class ExchangeCurrency {

    public static final String DT_CREATE_FIELD = "dt_create";

    @Id
    @Column
    private UUID uuid;

    @Column
    private Date dtCreate;

    @Column(precision = 10, scale = 2)
    private BigDecimal usd;

    @Column(precision = 10, scale = 2)
    private BigDecimal eur;

    @Column(precision = 10, scale = 2)
    private BigDecimal gel;

    @Column(precision = 10, scale = 2)
    private BigDecimal byn;

    @Column(precision = 10, scale = 2)
    private BigDecimal rub;

    @Column(precision = 10, scale = 10)
    private BigDecimal btc;

    @Column(precision = 10, scale = 10)
    private BigDecimal eth;



}

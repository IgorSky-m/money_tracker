package com.bot.calculation.entity.amount;

import com.bot.calculation.entity.currency.ExchangeCurrency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class Amount {

    public static final String UUID_FIELD = "uuid";
    public static final String DT_CREATE_FIELD = "dt_create";
    public static final String USER_ID_FIELD = "user_id";
    public static final String AMOUNT_FIELD = "amount";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String CURRENCY_FIELD = "currency";

    @Id
    @Column
    private UUID uuid;

    @Column
    private Date dtCreate;

    @Column
    private Long userId;

    @Column
    private BigDecimal amount;

    @Column
    private String description;

    @ManyToOne
    private ExchangeCurrency currency;

}

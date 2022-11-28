package com.bot.calculation.entity.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class CustomUser {

    @Id
    @Column
    private UUID uuid;

    @Column
    private Long telegramId;
}

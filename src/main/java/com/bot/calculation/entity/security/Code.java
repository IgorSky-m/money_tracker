package com.bot.calculation.entity.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class Code {

    @Id
    @Column
    private UUID uuid;

    @Column
    private UUID userId;

    @Column
    private Date timestamp;

    @Column
    private String password;
}

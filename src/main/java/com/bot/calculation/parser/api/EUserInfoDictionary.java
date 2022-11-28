package com.bot.calculation.parser.api;

import java.util.Arrays;

//todo перенести в бд
public enum EUserInfoDictionary {
    I("i", "и","Игорь", 1199168600L),
    M("m", "м","Максим", 189045130L);

    public final String en;
    public final String ru;
    public final String username;
    public final Long id;

    EUserInfoDictionary(String en, String ru, String username, Long id){
        this.en = en;
        this.ru = ru;
        this.username = username;
        this.id = id;
    }

    public static EUserInfoDictionary findById(Long id) {
        return Arrays.stream(EUserInfoDictionary.values())
                .filter(e -> e.id.equals(id))
                .findFirst()
                .orElse(null);
    }
}

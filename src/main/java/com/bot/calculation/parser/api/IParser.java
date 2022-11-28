package com.bot.calculation.parser.api;

public interface IParser<REQUEST, RESPONSE> {
    RESPONSE parse(REQUEST text);
}

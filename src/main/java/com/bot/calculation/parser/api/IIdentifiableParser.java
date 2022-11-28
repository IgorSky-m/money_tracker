package com.bot.calculation.parser.api;

import com.bot.calculation.executor.api.IIdentifiable;

public interface IIdentifiableParser<REQUEST, RESPONSE> extends IParser<REQUEST, RESPONSE>, IIdentifiable<EParserType> {
}

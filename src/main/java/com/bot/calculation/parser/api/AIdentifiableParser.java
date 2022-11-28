package com.bot.calculation.parser.api;

public abstract class AIdentifiableParser<REQUEST, RESPONSE> implements IIdentifiableParser<REQUEST, RESPONSE> {

    private final EParserType identifier;

    public AIdentifiableParser(EParserType identifier){
        this.identifier = identifier;
    }


    @Override
    public EParserType getIdentifier() {
        return identifier;
    }
}

package com.bot.calculation.parser;


import com.bot.calculation.parser.api.AIdentifiableParser;
import com.bot.calculation.parser.api.EParserType;
import com.bot.calculation.parser.api.IAliveTextParser;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component(AliveTextParser.PARSER_NAME)
public class AliveTextParser extends AIdentifiableParser<String, String> implements IAliveTextParser {

    public static final String PARSER_NAME = "AliveTextParser";

    public AliveTextParser() {
        super(EParserType.ALIVE);
    }

    @Override
    public String parse(String text) {
        String request = text.toLowerCase();
        HealthCheckWords healthCheck = Arrays.stream(HealthCheckWords.values())
                .filter(e -> e.request.startsWith(request))
                .findFirst()
                .orElse(null);
        if (healthCheck != null) {
            return healthCheck.response;
        }
        return null;
    }


    private enum HealthCheckWords{
        LIVE("жив", "ага"),
        HOW("ты как", "я норм"),
        SLEEP("спишь", "неа");

        private final String request;
        private final String response;

        HealthCheckWords(String request, String response){
            this.request = request;
            this.response = response;
        }
    }

}

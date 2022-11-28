package com.bot.calculation.parser;

import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.exception.ParserException;
import com.bot.calculation.parser.api.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component(AmountParser.PARSER_NAME)
public class AmountParser extends AIdentifiableParser<String, IParserResponse> implements ITextParser {

    public static final String PARSER_NAME = "AmountParser";
    private static final String PARSE_AMOUNT_ERR = "не смог распарсить число";

    public AmountParser() {
        super(EParserType.AMOUNT);
    }

    @Override
    public IParserResponse parse(String text) {
        ParseResponse.ParseResponseBuilder builder = ParseResponse.builder();
        String[] arguments = text.toLowerCase().split(" ");

        if (arguments.length == 0) {
            throw new IllegalArgumentException("текст для парсинга отсутствует");
        }

        BigDecimal amount = parseAmount(arguments[0]);
        if (amount == null) {
            throw new ParserException(PARSE_AMOUNT_ERR);
        }
        builder.amount(amount);

        String[] argumentsWithoutAmount = null;

        if (arguments.length > 1) {
            argumentsWithoutAmount = Arrays.copyOfRange(arguments, 1, arguments.length);
        }


        if (argumentsWithoutAmount != null) {
            StringBuilder description = null;
            for (String arg : argumentsWithoutAmount) {

                EUserInfoDictionary user = parseUserInfo(arg);
                if (user != null) {
                    builder.user(user);
                    continue;
                }

                ECurrencyCode currency = parseCurrency(arg);
                if (currency != null) {
                    builder.currency(currency);
                    continue;
                }

                if (description == null) {
                    description = new StringBuilder();
                    description.append(arg);
                    continue;
                }
                description.append(" ").append(arg);
            }
            if (description != null) {
                builder.description(description.toString());
            }
        }

        return builder.build();

    }

    private EUserInfoDictionary parseUserInfo(String text){
        return Arrays.stream(EUserInfoDictionary.values())
                .filter(e -> e.en.endsWith(text.toLowerCase()) || e.ru.startsWith(text.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    private BigDecimal parseAmount(String text){
        try {
            return BigDecimal.valueOf(Double.parseDouble(text));
        } catch (Exception e) {
            return null;
        }
    }


    private ECurrencyCode parseCurrency(String text) {
        return ECurrencyCode.safeFindCurrencyByValueIgnoreCase(text);
    }



//    @Override
//    public IParserResponse parse(String text) {
//        ParseResponse.ParseResponseBuilder builder = ParseResponse.builder();
//        String[] arr = text.toLowerCase().split(" ");
//
//        if (arr.length == 0) {
//            throw new IllegalArgumentException("текст для парсинга отсутствует");
//        }
//
//        if (arr.length == 1) {
//            BigDecimal amount = parseAmount(arr[0]);
//            if (amount == null) {
//                throw new ParserException(PARSE_AMOUNT_ERR);
//            }
//            return ParseResponse.builder()
//                    .amount(amount)
//                    .build();
//        }
//
//        UserInfoDictionary userInfo = Arrays.stream(UserInfoDictionary.values()).filter(e -> e != null &&
//                        Arrays.stream(arr)
//                                .anyMatch(
//                                        t -> t.startsWith(e.en) || t.startsWith(e.ru)))
//                .findFirst()
//                .orElseThrow(() -> new ParserException("Не понял, ты кто"));
//
//        BigDecimal amount = Arrays.stream(arr).map(this::parseAmount)
//                .filter(Objects::nonNull)
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException(PARSE_AMOUNT_ERR));
//
//        ECurrencyCode code = parseCode(ar);
//
//        return ParseResponse.builder()
//                .user(userInfo)
//                .amount(amount)
//                .build();
//    }
}


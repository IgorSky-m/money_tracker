package com.bot.calculation.entity.amount;

import com.bot.calculation.exception.CustomValidationException;
import com.bot.calculation.parser.api.EUserInfoDictionary;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class AmountValidator {

    private final MessageSource messageSource;

    public AmountValidator(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    void validateCreateRequest(AmountCreateRequest request){
        Map<String, String> errors = new HashMap<>();

        if (request.getAmount() == null) {
            errors.put(Amount.AMOUNT_FIELD, messageSource.getMessage("validation.error.empty.field", null, LocaleContextHolder.getLocale()));
        }

        if (request.getUserId() == null) {
            errors.put(Amount.USER_ID_FIELD, messageSource.getMessage("validation.error.empty.field", null, LocaleContextHolder.getLocale()));
        } else {
            boolean isUnknownUser = Arrays.stream(EUserInfoDictionary.values()).noneMatch(e -> e.id.equals(request.getUserId()));

            if (isUnknownUser) {
                errors.put(Amount.USER_ID_FIELD, messageSource.getMessage("validation.error.user_id.not.found", null, LocaleContextHolder.getLocale()));
            }
        }



        if (!errors.isEmpty()) {
            throw new CustomValidationException(errors);
        }
    }
}

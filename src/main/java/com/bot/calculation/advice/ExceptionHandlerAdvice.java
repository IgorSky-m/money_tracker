package com.bot.calculation.advice;

import com.bot.calculation.exception.CustomApplicationException;
import com.bot.calculation.exception.CustomServiceException;
import com.bot.calculation.exception.CustomValidationException;
import com.bot.calculation.exception.EntityNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Совет для перехвата ошибок и вывода нужного текста и статуса
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    private static final String ENUM_EXCEPTION = "not one of the values accepted for Enum class: ";
    private final MessageSource messageSource;

    public ExceptionHandlerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomValidationException.class)
    public Map<String, String> validationExceptionHandler(CustomValidationException ex) {
        if (ex.getStructuredMessages() == null || ex.getStructuredMessages().isEmpty()){
            String msg = messageSource.getMessage("validation.error.default", null, LocaleContextHolder.getLocale());
            return Collections.singletonMap("error_msg", msg);
        }
        return ex.getStructuredMessages();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String illegalArgumentException(IllegalArgumentException ex) {
        if (ex.getMessage() == null || ex.getMessage().isEmpty()){
            return messageSource.getMessage("error.bad.request.default", null, LocaleContextHolder.getLocale());
        }
        return ex.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(EntityNotFound.class)
    public void notFoundException() {
        System.out.println("NOT FOUND");
    }


    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void httpRequestMethodNotSupportedException() {}



    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomApplicationException.class)
    public String MethodArgumentNotValidExceptionHandler(CustomApplicationException ex) {
        String msg;
        if (ex.getMessage() == null || ex.getMessage().isEmpty()){
            msg = messageSource.getMessage("error.default", null, LocaleContextHolder.getLocale());
            return msg;
        }
        return ex.getMessage();
    }

    //Для отлова и логирования ошибок, которые мы по каким-либо причинам не словили ранее
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String unknownExceptionHandler(Exception ex) {
        String msg = messageSource.getMessage("error.default", null, LocaleContextHolder.getLocale());
        log.error(msg, ex);
        return msg;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public Map<String, String> clientErrorException(HttpClientErrorException e){
        return Collections.singletonMap("redirect_url", e.getMessage());
    }


    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String unknownExceptionHandler(HttpMessageNotReadableException ex) {
        String msg = messageSource.getMessage("invalid.params.body.request.err", null, LocaleContextHolder.getLocale());
        log.error(msg, ex);
        if (ex.getMessage() != null && ex.getMessage().contains("Cannot deserialize value of type")) {
            String[] split = ex.getMessage().split("not one of the values accepted for Enum class: ");
            String enumValues = null;
            for (String values : split) {
                if (values.startsWith("[")){
                    enumValues = values;
                    break;
                };
            }

            if (enumValues != null) {
                String[] values = enumValues.split(";");
                if (values.length > 0) {
                    msg = msg.trim() + ". " + ENUM_EXCEPTION + values[0];
                }
            }

        }
        return msg;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomServiceException.class)
    public String unknownExceptionHandler(CustomServiceException ex) {
        log.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String missingParamExceptionHandler(MissingServletRequestParameterException ex) {
        String msg = messageSource.getMessage("invalid.params.query.request.err", null, LocaleContextHolder.getLocale());
        log.error(msg, ex);
        return msg;
    }







}

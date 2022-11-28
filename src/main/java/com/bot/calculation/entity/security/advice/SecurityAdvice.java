package com.bot.calculation.entity.security.advice;

import com.bot.calculation.entity.security.annotations.Security;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Arrays;

@Aspect
@Component
public class SecurityAdvice {

    private static final String REDIRECT_PATH_TEMPLATE = "http://t.me/%s?start=%s";

    @Context
    @Autowired
    private HttpServletRequest request;

    @Context
    @Autowired
    private HttpServletResponse response;

    @Value("${app.security.auth.name}")
    private String authParamName;

    @Value("${com.bot.calculation.settings.NAME}")
    private String botName;

    @Pointcut("@annotation(security)")
    public void security(Security security){
    }

    @Before("security(security)")
    public void checkSecurity(Security security) throws IOException {
        if (!security.enabled()){
            return;
        }
        //todo Проверять соответствие куки авторизации
        Cookie[] cookies = request.getCookies();
        Cookie authCookie = null;
        if (cookies != null) {
            authCookie = Arrays.stream(cookies)
                    .filter(e -> e.getName().equals(authParamName))
                    .findFirst()
                    .orElse(null);
        }
        if (authCookie == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("redirect_url", String.format(REDIRECT_PATH_TEMPLATE, botName, authParamName));
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, String.format(REDIRECT_PATH_TEMPLATE, botName, authParamName));
        }
    }




}

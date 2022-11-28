package com.bot.calculation.sender;

import com.bot.calculation.sender.api.AMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class DefaultMessageSender extends AMessageSender {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageSender.class);

    public DefaultMessageSender(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}

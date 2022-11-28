package com.bot.calculation.entity.security.generators;

import com.bot.calculation.entity.security.generators.api.IPinCodeGenerator;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class PinCodeGenerator implements IPinCodeGenerator {
    
    //сделать так:
    //сохраяем таймстамп входа, берем первые 4 цифры хеш от тайм стамп + ид 
    // ставим проперти 5 минут , проверяем по этому таймстампу в течении пяти минут.
    @Override
    public String generate(String userId) {
        String s = ZonedDateTime.now() + userId;
        int i = Math.abs(s.hashCode());
        StringBuilder pin = new StringBuilder(String.valueOf(i));
        if (pin.length() < 4) {
            for (int i1 = pin.length(); i1 < 5; i1++) {
                pin.insert(0, "0");
            }
        }
        return pin.substring(0, 4);
    }
}

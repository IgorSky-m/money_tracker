package com.bot.calculation.entity.security;

import com.bot.calculation.entity.security.api.ICodeRepository;
import com.bot.calculation.entity.security.api.ISecurityService;
import com.bot.calculation.entity.security.generators.api.IPinCodeGenerator;
import com.bot.calculation.entity.user.CustomUser;
import com.bot.calculation.entity.user.api.ICustomUserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SecurityService implements ISecurityService {

    private final ICustomUserRepository repository;
    private final IPinCodeGenerator generator;
    private final ICodeRepository codeRepository;
    private final Long pinCodeLifetime;
    private final Integer authLifetime;
    private final String authParamName;

    @Context
    @Autowired
    private HttpServletRequest request;

    @Context
    @Autowired
    private HttpServletResponse response;

    public SecurityService (
            ICustomUserRepository repository,
            IPinCodeGenerator generator,
            ICodeRepository codeRepository,
            @Value("${app.security.pin_code.lifetime}") Long pinCodeLifetime,
            @Value("${app.security.auth.lifetime}") Integer authLifetime,
            @Value("${app.security.auth.name}") String authParamName
    ){
        this.repository = repository;
        this.generator = generator;
        this.codeRepository = codeRepository;
        this.pinCodeLifetime = pinCodeLifetime;
        this.authLifetime = authLifetime;
        this.authParamName = authParamName;
    }

    @Transactional
    @Override
    public String generatePinCode(Long userId) {
        if (userId == null) {
            return null;
        }

        CustomUser customUser = repository.findByTelegramId(userId)
                .orElse(null);

        if (customUser == null) {
            return null;
        }

        String pinCode = this.generator.generate(String.valueOf(userId));

        Optional<Code> codeByUserId = codeRepository.findByUserId(customUser.getUuid());
        Code code;
        if (codeByUserId.isPresent()){
            code = codeByUserId.get();
        } else {
            code = new Code();
            code.setUuid(UUID.randomUUID());
            code.setUserId(customUser.getUuid());
        }
        code.setTimestamp(new Date());
        code.setPassword(getEncodedPin(pinCode));

        codeRepository.save(code);

        return pinCode;
    }

    @Override
    public void authorize(String pin) {
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        if (isValidPinCode(pin, new Date())){
            Cookie cookie = new Cookie(authParamName, getEncodedPin(pin));
            cookie.setPath("/");
            cookie.setMaxAge(authLifetime);
            //cookie.setHttpOnly(true);
            response.addCookie(cookie);
            status = HttpServletResponse.SC_OK;
        }

        response.setStatus(status);
    }


    private boolean isValidPinCode(String pinCode, Date validateDate) {
        if (pinCode == null) {
            return false;
        }
        Optional<Code> optional = codeRepository.findByPassword(getEncodedPin(pinCode));

        if (optional.isPresent()) {
            Code code = optional.get();
            long lifetime = code.getTimestamp().getTime() + (pinCodeLifetime * 1000);
            return lifetime > validateDate.getTime();
        }

        return false;
    }


    private String getEncodedPin(String pinCode) {
        return DigestUtils.sha256Hex(pinCode);
    }
}

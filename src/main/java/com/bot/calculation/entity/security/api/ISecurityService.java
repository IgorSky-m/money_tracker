package com.bot.calculation.entity.security.api;

public interface ISecurityService {


    String generatePinCode(Long userId);
    void authorize(String pinCode);
}

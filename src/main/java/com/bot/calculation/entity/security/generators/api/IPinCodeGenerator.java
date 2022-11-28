package com.bot.calculation.entity.security.generators.api;

@FunctionalInterface
public interface IPinCodeGenerator {
    String generate(String userId);
}

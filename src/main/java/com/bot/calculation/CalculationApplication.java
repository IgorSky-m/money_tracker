package com.bot.calculation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan("com")
@SpringBootApplication
public class CalculationApplication {

    private static final Logger logger = LoggerFactory.getLogger(CalculationApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(CalculationApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
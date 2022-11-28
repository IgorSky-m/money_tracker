package com.bot.calculation.entity.currency.api;

import com.bot.calculation.entity.currency.ExchangeCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICurrencyRepository extends JpaRepository<ExchangeCurrency, UUID>, JpaSpecificationExecutor<ExchangeCurrency> {
    Optional<ExchangeCurrency> findFirstByOrderByDtCreateDesc();
    Optional<ExchangeCurrency> findFirstByDtCreate(Date dtCreate);
}

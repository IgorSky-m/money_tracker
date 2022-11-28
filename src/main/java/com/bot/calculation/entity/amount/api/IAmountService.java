package com.bot.calculation.entity.amount.api;

import com.bot.calculation.entity.amount.Amount;
import com.bot.calculation.entity.amount.AmountCreateRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IAmountService {

    void save(Amount amount);
    UUID create(AmountCreateRequest amount);

    Amount getAmountById(UUID id);

    List<Amount> getAllWithFilterProps(UUID id, Date dtCreate, Long userId, Double amount, String description);
    List<Amount> getAll();
    List<Amount> getByUserId(Long userId);

    List<Amount> getByMonth(int mount);

    List<Amount> getByYearAndMonth(int year, int month);

    void delete(UUID uuid);

    void update(Amount amount);

    List<Amount> getByPeriod(Date dtFrom, Date dtTo);

    void updateDescription(UUID uuid, Map<String, String> description);

}

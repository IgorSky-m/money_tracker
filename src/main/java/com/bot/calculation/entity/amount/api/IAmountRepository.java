package com.bot.calculation.entity.amount.api;

import com.bot.calculation.entity.amount.Amount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface IAmountRepository extends JpaRepository<Amount, UUID>, JpaSpecificationExecutor<Amount> {
    List<Amount> findAllByUserId(Long userId);
    
    List<Amount> findAllByDtCreateBetween(Date from, Date to);
    List<Amount> findAllByDtCreateBetweenOrderByDtCreateAsc(Date from, Date to);
    List<Amount> findAllByDtCreateBetweenOrderByDtCreateDesc(Date from, Date to);

}

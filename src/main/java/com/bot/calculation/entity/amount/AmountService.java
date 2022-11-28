package com.bot.calculation.entity.amount;

import com.bot.calculation.entity.amount.api.IAmountRepository;
import com.bot.calculation.entity.amount.api.IAmountService;
import com.bot.calculation.entity.currency.ExchangeCurrency;
import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.entity.currency.api.ICurrencyService;
import com.bot.calculation.exception.CustomServiceException;
import com.bot.calculation.exception.CustomValidationException;
import com.bot.calculation.exception.EntityNotFound;
import com.bot.calculation.exchanger.api.IExchanger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


@Transactional(readOnly = true)
@Service
public class AmountService implements IAmountService {

    private static final Logger logger = LoggerFactory.getLogger(AmountService.class);

    private final IAmountRepository repository;
    private final MessageSource messageSource;
    private final AmountValidator validator;
    private final IExchanger exchanger;
    private final ICurrencyService currencyService;
    public AmountService(
            IAmountRepository repository,
            MessageSource messageSource,
            AmountValidator validator,
            IExchanger exchanger,
            ICurrencyService currencyService
    ){
        this.repository = repository;
        this.messageSource = messageSource;
        this.validator = validator;
        this.exchanger = exchanger;
        this.currencyService = currencyService;
    };


    @Transactional
    @Override
    public void save(Amount amount) {
        try {
            repository.save(amount);
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.create", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Transactional
    @Override
    public UUID create(AmountCreateRequest request) {
        try {

            validator.validateCreateRequest(request);

            UUID uuid = UUID.randomUUID();

            Amount amount = new Amount();
            amount.setUuid(uuid);
            amount.setDtCreate(request.getDate() == null ? new Date() : request.getDate());
            amount.setDescription(request.getDescription());
            BigDecimal resultAmount = request.getAmount();

            if (request.getCurrency() != null) {
                resultAmount = exchanger.exchange(request.getCurrency(), ECurrencyCode.GEL, resultAmount);
            }

            amount.setAmount(resultAmount);
            amount.setUserId(request.getUserId());

            ExchangeCurrency actualCurrency = currencyService
                    .findCurrencyByDate(amount.getDtCreate())
                    .orElse(null);

            if (actualCurrency == null) {
                actualCurrency = currencyService.getLastCurrency();
            }

            amount.setCurrency(actualCurrency);
            repository.save(amount);
            return uuid;
        } catch (CustomValidationException e) {
            throw e;
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.create", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public Amount getAmountById(UUID id) {
        try {
            return repository
                    .findById(id)
                    .orElseThrow(
                            () -> new EntityNotFound(
                                    messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale())
                            )
                    );
        } catch (IllegalArgumentException | EntityNotFound e) {
            throw e;
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public List<Amount> getAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public List<Amount> getAllWithFilterProps(UUID id, Date dtCreate, Long userId, Double amount, String description) {
       try {
           Amount filterProps = new Amount();
           filterProps.setUuid(id);
           filterProps.setDtCreate(dtCreate);
           filterProps.setUserId(userId);
           filterProps.setAmount(amount == null ? null : BigDecimal.valueOf(amount));
           filterProps.setDescription(description);
           return repository.findAll(Example.of(filterProps));
       } catch (Exception e) {
           String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
           logger.error(msg,e);
           throw new CustomServiceException(msg);
       }
    }

    @Override
    public List<Amount> getByUserId(Long userId) {
        try {
            return repository.findAllByUserId(userId);
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public List<Amount> getByMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        //Начало дня
       return getByYearAndMonth(year, month);
    }



    @Override
    public List<Amount> getByYearAndMonth(int year, int month){
        ZonedDateTime from = ZonedDateTime.of(year, month, 1, 0, 0, 0,0, ZoneId.systemDefault());
        ZonedDateTime to = from.plusMonths(1);
        return getByPeriod(Date.from(from.toInstant()), Date.from(to.toInstant()));
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        try {
            repository.deleteById(uuid);
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.delete", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Transactional
    @Override
    public void update(Amount amount) {
        try {
            repository.save(amount);
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.update", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public List<Amount> getByPeriod(Date dtFrom, Date dtTo) {
        try {
            return repository.findAllByDtCreateBetweenOrderByDtCreateAsc(dtFrom, dtTo);
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Transactional
    @Override
    public void updateDescription(UUID uuid, Map<String, String> map) {
        if (!map.containsKey(Amount.DESCRIPTION_FIELD)){
            String msg = messageSource.getMessage("invalid.params.body.request.err", null, LocaleContextHolder.getLocale());
            throw new CustomServiceException(msg);
        }
        Amount amount = getAmountById(uuid);
        amount.setDescription(map.get(Amount.DESCRIPTION_FIELD));
        update(amount);
    }
}

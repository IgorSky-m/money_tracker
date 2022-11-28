package com.bot.calculation.entity.currency;

import com.bot.calculation.entity.currency.api.ICurrencyRepository;
import com.bot.calculation.entity.currency.api.ICurrencyService;
import com.bot.calculation.exception.CustomServiceException;
import com.bot.calculation.exception.EntityNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class CurrencyService implements ICurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private final ICurrencyRepository repository;
    private final MessageSource messageSource;

    public CurrencyService(ICurrencyRepository repository, MessageSource messageSource){
        this.repository = repository;
        this.messageSource = messageSource;
    }


    @Override
    public ExchangeCurrency getLastCurrency() {
        try {
            return repository.findFirstByOrderByDtCreateDesc()
                    .orElse(null);
        } catch (Exception e){
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public ExchangeCurrency getCurrencyByDate(Date dtCreate) {
        try {
            return repository.findFirstByDtCreate(dtCreate)
                    .orElseThrow(EntityNotFound::new);
        } catch (EntityNotFound e) {
            throw e;
        } catch (Exception e){
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public Optional<ExchangeCurrency> findCurrencyByDate(Date date){
        try {
            return repository.findFirstByDtCreate(date);
        } catch (Exception e){
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Transactional
    @Override
    public void create(ExchangeCurrency currency) {
        try {
            this.repository.save(currency);
        } catch (Exception e){
            String msg = messageSource.getMessage("error.crud.create", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Transactional
    @Override
    public void update(ExchangeCurrency currency) {
            create(currency);
    }

    @Override
    public List<ExchangeCurrency> getAll() {
        try {
            return repository.findAll();
        } catch (Exception e){
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }
}

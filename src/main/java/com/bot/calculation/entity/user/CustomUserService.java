package com.bot.calculation.entity.user;

import com.bot.calculation.entity.user.api.ICustomUserRepository;
import com.bot.calculation.entity.user.api.ICustomUserService;
import com.bot.calculation.exception.CustomServiceException;
import com.bot.calculation.exception.EntityNotFound;
import com.bot.calculation.parser.api.EUserInfoDictionary;
import org.hibernate.event.spi.RefreshEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CustomUserService implements ICustomUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserService.class);


    private final ICustomUserRepository repository;
    private final MessageSource messageSource;

    public CustomUserService(
            ICustomUserRepository repository,
            MessageSource messageSource
            ){
        this.repository = repository;
        this.messageSource = messageSource;

    }

    @Transactional
    @Override
    public void create(CustomUser customUser) {
        try {
            repository.save(customUser);
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.create", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public CustomUser getUserTgId(Long tgId) {
        try {
            return repository.findByTelegramId(tgId)
                    .orElseThrow(EntityNotFound::new);
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public List<CustomUser> getAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }

    @Override
    public Optional<CustomUser> findUserByTgId(Long tgId) {
        try {
            return repository.findByTelegramId(tgId);
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.crud.read", null, LocaleContextHolder.getLocale());
            logger.error(msg,e);
            throw new CustomServiceException(msg);
        }
    }


    @EventListener({ContextRefreshedEvent.class})
    @Transactional
    public void postConstruct(){
        EUserInfoDictionary[] values = EUserInfoDictionary.values();
        List<Long> hardcodeTgIdList = Arrays.stream(values)
                .map(e -> e.id)
                .collect(Collectors.toList());

        Set<Long> foundedTgIdList = this.repository.findAll()
                .stream()
                .map(CustomUser::getTelegramId)
                .collect(Collectors.toSet());

        List<CustomUser> usersForCreate = hardcodeTgIdList.stream()
                .filter(e -> !foundedTgIdList.contains(e))
                .map(e -> {
                    CustomUser customUser = new CustomUser();
                    customUser.setUuid(UUID.randomUUID());
                    customUser.setTelegramId(e);
                    return customUser;
                })
                .collect(Collectors.toList());

        this.repository.saveAll(usersForCreate);
    }
}

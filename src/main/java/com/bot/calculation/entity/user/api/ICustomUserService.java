package com.bot.calculation.entity.user.api;

import com.bot.calculation.entity.user.CustomUser;

import java.util.List;
import java.util.Optional;

public interface ICustomUserService {
    void create(CustomUser customUser);
    CustomUser getUserTgId(Long tgId);
    List<CustomUser> getAll();
    Optional<CustomUser> findUserByTgId(Long tgId);

}

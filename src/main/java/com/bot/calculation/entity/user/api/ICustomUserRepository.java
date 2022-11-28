package com.bot.calculation.entity.user.api;

import com.bot.calculation.entity.user.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICustomUserRepository extends JpaRepository<CustomUser, UUID> {
    Optional<CustomUser> findByTelegramId(Long telegramId);
}

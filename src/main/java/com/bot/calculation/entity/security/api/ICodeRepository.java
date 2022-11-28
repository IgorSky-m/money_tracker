package com.bot.calculation.entity.security.api;

import com.bot.calculation.entity.security.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICodeRepository extends JpaRepository<Code, UUID> {
    Optional<Code> findByPassword(String password);
    Optional<Code> findByUserId(UUID userId);
}

package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate,Long> {

    int deleteByTimestampBefore(LocalDate timestamp);
    Optional<LocalDate> findMaxTimestamp();
    boolean existsByTimestampAndCurUnit(LocalDate parse, String curUnit);
}

package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate,Long> {

    int deleteByTimestampBefore(LocalDate timestamp);

    @Query("SELECT MAX(e.timestamp) FROM ExchangeRate e")
    Optional<LocalDate> findMaxTimestamp();

    boolean existsByTimestampAndCurUnit(LocalDate parse, String curUnit);
}

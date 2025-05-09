package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

}
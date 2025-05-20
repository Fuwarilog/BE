package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate,Long> {
}

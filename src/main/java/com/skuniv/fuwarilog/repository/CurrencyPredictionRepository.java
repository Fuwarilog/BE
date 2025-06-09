package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.CurrencyPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyPredictionRepository extends JpaRepository<CurrencyPrediction, Long> {
}
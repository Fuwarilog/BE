package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.repository.CurrencyPredictionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@AllArgsConstructor
public class ExchangeRateService {

    private final CurrencyPredictionRepository exchangeRateRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldRates() {
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        exchangeRateRepository.deleteByPredictDateBefore(threeMonthsAgo);
    }
}

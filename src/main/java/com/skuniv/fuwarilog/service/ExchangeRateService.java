package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.domain.ExchangeRate;
import com.skuniv.fuwarilog.repository.CurrencyPredictionRepository;
import com.skuniv.fuwarilog.repository.ExchangeRateRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    /**
     * @implSpec 데이터 스케쥴러
     */
    @Scheduled(cron = "0 0 11 * * ?")
    public void deleteOldRates() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        int result = exchangeRateRepository.deleteByTimestampBefore(threeMonthsAgo);
        log.info("3개월 이상된 환율 {}개 삭제", result);
    }

    /**
     * @implSpec 실시간 환율 데이터 저장
     * @param data 환율 데이터
     */
    public void saveExchangeRate(Map<String, Object> data) {
        ExchangeRate rate = new ExchangeRate();
        rate.setCurUnit((String)data.get("curUnit"));
        rate.setCurNm((String)data.get("curNm"));
        rate.setDealBasR(Double.valueOf((String)data.get("dealBasR")));
        rate.setTimestamp(LocalDateTime.now());
        rate.setUpdatedAt(LocalDateTime.now());
        rate.setCreatedAt(LocalDateTime.now());
        exchangeRateRepository.save(rate);
    }


}

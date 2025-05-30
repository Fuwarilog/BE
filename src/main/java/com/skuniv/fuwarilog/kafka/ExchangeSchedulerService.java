package com.skuniv.fuwarilog.kafka;

import com.skuniv.fuwarilog.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeSchedulerService {

    private final ExchangeRateRepository exchangeRateRepository;

    /**
     * @implSpec 데이터 스케쥴러
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldRates() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        int result = exchangeRateRepository.deleteByTimestampBefore(sixMonthsAgo);
        log.info("6개월 이상된 환율 {}개 삭제", result);
    }
}

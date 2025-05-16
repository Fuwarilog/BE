package com.skuniv.fuwarilog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skuniv.fuwarilog.domain.ExchangeRate;
import com.skuniv.fuwarilog.dto.ExchangeRateRequest;
import com.skuniv.fuwarilog.repository.CurrencyPredictionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaConsumerService {

    private CurrencyPredictionRepository currencyPredictionRepository;

    @KafkaListener(topics = "predict-week-rates", groupId = "fuwarilog-group")
    public void consume(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ExchangeRateRequest.ExchangeRateDTO dto = mapper.readValue(message, ExchangeRateRequest.ExchangeRateDTO.class);

            ExchangeRate exchangeRate = ExchangeRate.builder()
                    .currency(dto.getCurrencyId())
                    .baseCurrency(dto.getBaseCurrency())
                    .rateValue(dto.getRateValue())
                    .percentChange(dto.getPercentChange())
                    .timestamp(dto.getTimeStamp())
                    .build();

            currencyPredictionRepository.save(exchangeRate);

        } catch (Exception e) {
            log.error("Kafka message parse error", e);
            throw new RuntimeException(e);
        }
    }

}

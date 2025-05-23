package com.skuniv.fuwarilog.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.ExchangeRate;
import com.skuniv.fuwarilog.dto.ExchangeRateRequest;
import com.skuniv.fuwarilog.repository.CurrencyPredictionRepository;
import com.skuniv.fuwarilog.repository.ExchangeRateRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

//    private final ObjectMapper objectMapper;
//    private final ExchangeRateRepository exchangeRateRepository;

//    /**
//     * @implSpec 환율 데이터 Consumer
//     * @param message 토픽에서 받은 메세지
//     */
//    @KafkaListener(topics = "exchange-prediction-rate", groupId = "fuwarilog-group")
//    public void consume(String message) {
//        try {
//            JsonNode node = objectMapper.readTree(message);
//
//            ExchangeRate rate = new ExchangeRate();
//            rate.setCurUnit(node.get("cur_unit").asText());
//            rate.setCurNm(node.get("cur_nm").asText());
//            rate.setDealBasR(Double.parseDouble(node.get("deal_bas_r").asText()));
//            rate.setTimestamp(LocalDateTime.parse(node.get("timestamp").asText()));
//
//            exchangeRateRepository.save(rate);
//        } catch (Exception e) {
//            log.error("Kafka 메시지 소비 중 오류 발생", e);
//            throw new BadRequestException(ErrorResponseStatus.REQUEST_ERROR);
//        }
//    }
}

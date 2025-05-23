package com.skuniv.fuwarilog.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.dto.ExchangeRateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${exchange.api.key}")
    private String apiKey;

    /**
     * @implSpec 실시간 환율 데이터 연동 및 전달
     */
    @Scheduled(cron = "0 0 11 * * *")
    public void fetchAndSendExchangeRates() {
        // 환율 실시간 API 연동
        String url = UriComponentsBuilder.fromHttpUrl("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON")
                .queryParam("authkey", apiKey)
                //.queryParam("searchdate", "20250522")
                .queryParam("searchdate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .queryParam("data", "AP01")
                .toUriString();

        log.info("Fetching exchange rates from Kafka ...");
        log.info(url);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());

                log.info(root.toString());

                for (JsonNode node : root) {
                    if(!node.path("result").asText().equals("1")) continue;

                    ExchangeRateRequest.ExchangeRateDTO dto = new ExchangeRateRequest.ExchangeRateDTO();
                    dto.setCurUnit(node.get("cur_unit").asText());
                    dto.setDealBasR(node.get("deal_bas_r").asText());
                    dto.setCurNm(node.get("cur_nm").asText());
                    dto.setTimeStamp(LocalDateTime.now().toString());

                    String json = mapper.writeValueAsString(dto);
                    kafkaTemplate.send("exchange_rate", dto.getCurUnit(), json);
                }
            }

        } catch (Exception e) {
            log.error("OpenAPI 호출 또는 Kafka 전송 실패", e);
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }
}
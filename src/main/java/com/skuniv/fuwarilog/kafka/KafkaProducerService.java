package com.skuniv.fuwarilog.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.dto.ExchangeRate.ExchangeRateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
    @Scheduled(cron = "0 0/30 * * * *")
    public void fetchAndSendExchangeRates() {
        // 환율 실시간 API 연동
        try{
            log.info("Start fetchAndSendExchangeRates");

            for (LocalDate date = LocalDate.parse("2024-12-31"); !date.isEqual(LocalDate.now()); date = date.plusDays(1)) {
                String uri = UriComponentsBuilder.fromHttpUrl("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON")
                        .queryParam("authkey", apiKey)
                        .queryParam("searchdate", date)
                        .queryParam("data", "AP01")
                        .toUriString();

                log.info("Fetching exchange rates from Kafka ...");
                log.info(uri);

                ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

                log.info(response.getBody());

                List<String> currency = Arrays.asList("USD", "JPY(100)", "CNH", "KRW", "CNY");

                if (response.getStatusCode() == HttpStatus.OK) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.getBody());

                    log.info(root.toString());

                    for (JsonNode node : root) {
                        if (!node.path("result").asText().equals("1")) continue;

                        if (currency.contains(node.path("cur_unit").asText())) {

                            ExchangeRateRequest.ExchangeRateDTO dto = new ExchangeRateRequest.ExchangeRateDTO();
                            dto.setCurUnit(node.get("cur_unit").asText());
                            dto.setDealBasR(node.get("deal_bas_r").asText());
                            dto.setTimeStamp(LocalDateTime.now().toString());

                            log.info(node.get("cur_unit").toString());
                            log.info("Sending data to Kafka topic...");

                            String json = mapper.writeValueAsString(dto);
                            kafkaTemplate.send("exchange_value_rate", dto.getCurUnit(), json)
                                    .whenComplete((result, e) -> {
                                        if (e != null) {
                                            log.error(e.getMessage());
                                        } else {
                                            log.info(result.toString());
                                        }
                                    });
                        }
                    }
                }
            }

        } catch (Exception e) {
                log.error("OpenAPI 호출 또는 Kafka 전송 실패", e);
                throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }
}
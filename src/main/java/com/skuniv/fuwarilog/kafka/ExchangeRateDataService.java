package com.skuniv.fuwarilog.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.ExchangeRate;
import com.skuniv.fuwarilog.dto.ExchangeRate.ExchangeRateRequest;
import com.skuniv.fuwarilog.repository.ExchangeRateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ExchangeRateDataService {

    private final RestTemplate restTemplate;
    private final ExchangeRateRepository exchangeRateRepository;

    @Value("${exchange.api.key}")
    private String apiKey;

    /**
     * @implSpec 실시간 환율 데이터 연동 및 전달
     */
    @Scheduled(cron = "0/15 * * * * *")
    public void fetchAndSendExchangeRates() {
        // 환율 실시간 API 연동
        try{
            log.info("[1] Start fetchAndSendExchangeRates");

            LocalDate latestSavedDate = exchangeRateRepository.findMaxTimestamp()
                    .orElse(LocalDate.parse("2024-12-30"));

            for (LocalDate date = latestSavedDate.plusDays(1); !date.isAfter(LocalDate.now()); date = date.plusDays(1)) {
                log.info(date.toString());

                String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                String uri = UriComponentsBuilder.fromUriString("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON")
                        .queryParam("authkey", apiKey)
                        .queryParam("searchdate", formattedDate)
                        .queryParam("data", "AP01")
                        .toUriString();

                log.info("[2] Fetching exchange rates from Kafka ...");
                log.info(uri);

                ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
                log.info("restTemplate Body: {}", response.getBody());

                List<String> currency = Arrays.asList("USD", "JPY(100)", "CNH");
                Map<String, String> CurrencyToCountry = new HashMap<>() {{
                    put("USD", "미국");
                    put("JPY", "일본");
                    put("CNH", "중국");
                }};

                // (1) null이 아닌 경우 데이터 받음
                if (!Objects.requireNonNull(response.getBody()).isEmpty()) {

                    if (response.getStatusCode() == HttpStatus.OK) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(response.getBody());

                        for (JsonNode node : root) {
                            if (!node.path("result").asText().equals("1")) continue;

                            if (currency.contains(node.path("cur_unit").asText())) {
                                ExchangeRateRequest.ExchangeRateDTO dto = new ExchangeRateRequest.ExchangeRateDTO();

                                double exchange_dbr = Double.parseDouble(node.path("deal_bas_r").asText().replace(",", ""));

                                dto.setCurUnit(node.get("cur_unit").asText());
                                dto.setDealBasR(Double.toString(exchange_dbr));
                                dto.setTimeStamp(formattedDate);

                                boolean exists = exchangeRateRepository.existsByTimestampAndCurUnit(LocalDate.parse(dto.getTimeStamp()), dto.getCurUnit());

                                if (exists) {
                                    log.info("중복 데이터 제외: {}, {}", dto.getTimeStamp(), dto.getCurUnit());
                                    continue;
                                }

                                log.info("[3] 데이터베이스에 저장...");

                                ExchangeRate exchangeRate = new ExchangeRate();
                                exchangeRate.setCurNm(CurrencyToCountry.get(dto.getCurUnit()));
                                exchangeRate.setCurUnit(dto.getCurUnit());
                                exchangeRate.setDealBasR(Double.parseDouble(dto.getDealBasR()));
                                exchangeRate.setTimestamp(LocalDate.parse(dto.getTimeStamp()));

                                exchangeRateRepository.save(exchangeRate);
                                log.info("[4] 데이터베이스에 저장 완료");
                            }
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
package com.skuniv.fuwarilog.service;

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
     * 매일 오전 11시에 환율 값 업데이트
     * 공휴일, 주말에는 은행 업무 휴무이므로 null
     */
    @Scheduled(cron = "0 0 11 * * *") // 매일 오전 11시(한국수출입 은행 업데이트 시간)
    public void fetchAndSendExchangeRates() {
        log.info("[1] Start fetchAndSendExchangeRates");

        // 1) 현재 날짜와 어제 날짜 기준 설정
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 2) DB에서 저장된 가장 마지막 환율 날짜 조회
        LocalDate latestSavedDate = exchangeRateRepository.findMaxTimestamp()
                .orElse(LocalDate.parse("2024-12-30")); // 초기값 설정

        log.info("[2] Last saved date: {}", latestSavedDate);

        // 3) 어제 날짜의 데이터가 저장되어 있지 않은 경우 → 누락 가능성 → 백필링
        if (latestSavedDate.isBefore(yesterday)) {
            log.info("[3] Missing data detected. Start backfilling from {} to {}", latestSavedDate.plusDays(1), yesterday);

            for (LocalDate date = latestSavedDate.plusDays(1); !date.isAfter(yesterday); date = date.plusDays(1)) {
                fetchExchangeRateForDate(date);
            }
        } else {
            // 최신 날짜(오늘 또는 어제)에 대한 데이터 수집 시도
            log.info("[3] No missing data. Try to fetch today's data: {}", today);
            fetchExchangeRateForDate(today);
        }
    }

    // 스케쥴링과 보존 정책을 함께 사용, 삭제 기준은 보존 정책에 따른다. -> 테이블 파티셔닝도 필요하다고 함
    // spirng cron은 개발 환경에서만 사용, 배포 환경은 mysql event schedule 계정으로 관리
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanUp() {
        LocalDate cutoff = LocalDate.now().minusMonths(6);
        exchangeRateRepository.deleteByTimestampBefore(cutoff);
    }

    public void fetchExchangeRateForDate(LocalDate date) {
        // 환율 실시간 API 연동
        try{
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
                put("CNY", "중국");
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

                                if ((node.path("cur_unit").asText()).equals("JPY(100)")) {
                                    dto.setCurUnit("JPY");
                                    dto.setDealBasR(Double.toString(exchange_dbr));
                                    dto.setTimeStamp(formattedDate);

                                } else if ((node.path("cur_unit").asText()).equals("CNH")) {
                                    dto.setCurUnit("CNY");
                                    dto.setDealBasR(Double.toString(exchange_dbr));
                                    dto.setTimeStamp(formattedDate);

                                } else {
                                    dto.setCurUnit(node.get("cur_unit").asText());
                                    dto.setDealBasR(Double.toString(exchange_dbr));
                                    dto.setTimeStamp(formattedDate);
                                }


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

        } catch (Exception e) {
                log.error("OpenAPI 호출 실패", e);
                throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }
}
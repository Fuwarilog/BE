package com.skuniv.fuwarilog.dto;

import com.skuniv.fuwarilog.domain.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ExchangeRateRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "RES1. 환율 정보 전달 DTO")
    public static class ExchangeRateDTO {
        Currency currencyId;
        String currencyCode;
        String baseCurrency;
        Double rateValue;
        Double percentChange;
        LocalDateTime timeStamp;
    }

}

package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

public class ExchangeRateRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "RES1. 환율 정보 전달 DTO")
    public static class ExchangeRateDTO {
        String currencyCode;
        String baseCurrency;
        Double rateValue;
        Double percentChange;
        LocalDateTime timeStamp;
    }

}

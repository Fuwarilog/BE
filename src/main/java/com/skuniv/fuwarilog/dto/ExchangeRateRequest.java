package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

public class ExchangeRateRequest {

    @Getter
    @Setter
    @Schema(description = "RES1. 환율 정보 전달 DTO")
    public static class ExchangeRateDTO {
        private String curUnit; // 통화 코드
        private String curNm;   // 국가명
        private String dealBasR; // 매매 기준율
        private String timeStamp; // 전송 시간
    }

}

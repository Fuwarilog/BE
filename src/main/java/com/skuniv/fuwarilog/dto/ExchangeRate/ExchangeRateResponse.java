package com.skuniv.fuwarilog.dto.ExchangeRate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ExchangeRateResponse {

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 1. 환율 정보 조회 DTO")
    public static class ExchangeRateDTO {
        private String curUnit; // 통화 코드
        private String curNm;   // 국가명
        private String dealBasR; // 매매 기준율
        private String timeStamp; // 전송 시간
    }

}

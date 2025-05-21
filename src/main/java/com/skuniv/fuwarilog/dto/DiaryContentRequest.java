package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class DiaryContentRequest {

    @Getter
    @Setter
    @Schema(title = "REQ 01: 다이어리 작성 DTO")
    public class ContentDTO {
        @Schema(description = "다이어리 내용")
        private String content;
    }
}

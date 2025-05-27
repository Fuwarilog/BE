package com.skuniv.fuwarilog.dto.DiaryContent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

public class DiaryContentRequest {

    @Getter
    @Setter
    @Schema(title = "REQ 01: 다이어리 작성 DTO")
    public static class ContentDTO {
        @Schema(description = "다이어리 내용")
        private String content;
    }
}

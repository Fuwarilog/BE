package com.skuniv.fuwarilog.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class DiaryContentRequest {

    @Getter
    @Setter
    @Data
    public class ContentDTO {
        private String content;
    }
}

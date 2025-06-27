package com.skuniv.fuwarilog.dto.DiaryContent;

import com.skuniv.fuwarilog.domain.DiaryContent;
import com.skuniv.fuwarilog.domain.DiaryList;
import com.skuniv.fuwarilog.dto.DiaryList.DiaryListResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DiaryContentResponse {

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 1. 특정 다이어리 이미지 반환 DTO")
    public static class DiaryPhotoResDTO {
        private List<String> imageUrls;

        public static DiaryContentResponse.DiaryPhotoResDTO from(DiaryContent diaryContent) {
            return new DiaryContentResponse.DiaryPhotoResDTO(
                    diaryContent.getImageUrls());
        }
    }
}

package com.skuniv.fuwarilog.domain;

import jakarta.persistence.Id;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Setter
@Document(collection = "diary_content")
public class DiaryContent {
    @Id
    private String contentId;

    @Field("list_id")
    private long diaryListId;

    @Field("content")
    private String content;

    @Field("image_urls")
    private List<String> imageUrls;

    private MapActivityMetadata mapActivity;

    @Field("created_at")
    private LocalDate createdAt;

    @Field("updated_at")
    private LocalDate updatedAt;
}
package com.skuniv.fuwarilog.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "diary_content")
public class DiaryContent {
    @Id
    private String id;

    @Field("list_id")
    private long diaryListId;

    @Field("content")
    private String content;

    @Field("image_urls")
    private List<String> imageUrls;

    @Field("created_at")
    private LocalDate createdAt;

    @Field("updated_at")
    private LocalDate updatedAt;
}
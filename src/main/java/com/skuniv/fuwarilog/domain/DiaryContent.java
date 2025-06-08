package com.skuniv.fuwarilog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "diary_content")
public class DiaryContent {
    @Id
    private String id;

    @Field("user_id")
    private long userId;

    @Field("list_id")
    private long diaryListId;

    @Field("google_event_id")
    private String googleEventId;

    @Field("trip_date")
    private LocalDate tripDate;

    @Field("content")
    private String content;

    @Field("image_urls")
    private List<String> imageUrls;

    @Field("tags")
    private List<LocationTag> tags;
}

package com.skuniv.fuwarilog.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationTag {

    @Field("place_name")
    private String placeName;

    private String address;

    @Field("place_url")
    private String placeUrl;

    private double latitude;

    private double longitude;

    @Field("tags_text")
    private String tagText;
}

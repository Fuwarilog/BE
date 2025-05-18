package com.skuniv.fuwarilog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "visited_routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitedRouteDocument {
    @Id
    private String id;

    @Field("user_id")
    private Long userId;

    @Field("origin")
    private String origin;

    @Field("destination")
    private String destination;

    @Field("distance_text")
    private String distanceText;

    @Field("distance_value")
    private int distanceValue;

    @Field("duration_text")
    private String durationText;

    @Field("duration_value")
    private int durationValue;

    @Field("trip_date")
    private LocalDate tripDate;
}

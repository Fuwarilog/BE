package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class Map extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "map_history_id", nullable = false)
    private long id;

    @Column(name = "place_name", columnDefinition = "VARCHAR(255)")
    private String placeName;

    @Column(name = "address", columnDefinition = "VARCHAR(255)")
    private String address;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "bookmarked")
    private boolean bookmarked;

    @Column(name = "searched_at")
    @ColumnDefault(value = "CurrentTimestamp")
    private LocalDateTime searchedAt;
}

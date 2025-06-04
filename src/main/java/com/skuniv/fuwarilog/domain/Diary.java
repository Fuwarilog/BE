package com.skuniv.fuwarilog.domain;

import com.skuniv.fuwarilog.domain.enums.TripStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name= "diary")
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="diary_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="trip_id")
    private Trip trip;

    @Column(name="title", columnDefinition = "VARCHAR(256)")
    private String title;

    @Column(name="country", columnDefinition = "VARCHAR(64)")
    private String country;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @Column(name="is_active")
    private TripStatus isActive;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<DiaryList> diaryLists = new ArrayList<>();

}

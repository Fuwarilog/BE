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
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name= "trip")
public class Trip extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="tripId", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name = "google_event_id")
    private String googleEventId;

    @Column(name = "title", columnDefinition = "VARCHAR(64)")
    private String title;

    @Column(name = "description", columnDefinition = "VARCHAR(64)")
    private String description;

    @Column(name="country", columnDefinition = "VARCHAR(64)")
    private String country;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<CurrencyPrediction> currencyPredictions = new ArrayList<>();
}

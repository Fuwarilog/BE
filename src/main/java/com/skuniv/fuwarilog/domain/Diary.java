package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<DiaryList> diaryLists = new ArrayList<>();

}

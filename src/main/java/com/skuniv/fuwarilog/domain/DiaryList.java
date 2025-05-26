package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DiaryList extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="diarylist_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "is_public", columnDefinition = "boolean default 'False'")
    private Boolean isPublic;
}

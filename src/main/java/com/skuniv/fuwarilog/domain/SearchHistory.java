package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.security.Timestamp;

@Entity
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class SearchHistory extends  BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_history_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "searched_at")
    private Timestamp searchedAt;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;
}

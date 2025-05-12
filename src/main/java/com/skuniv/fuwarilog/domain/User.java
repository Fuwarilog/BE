package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", nullable=false)
    private long id;

    @Column(name="email", columnDefinition = "VARCHAR(64)")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="refresh_token")
    private String refreshToken;

    @Column(name="name", columnDefinition = "VARCHAR(64)")
    private String name;

    @Column(name="provider", columnDefinition = "VARCHAR(64)")
    private String provider;

    @Column(name="pricture_url")
    private String pictureUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Trip> trips = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostBookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostLike> likes = new ArrayList<>();

}

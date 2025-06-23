package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Struct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name= "user")
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

    @Column(name = "google_access_token")
    private String googleAccessToken;

    @Column(name="name", columnDefinition = "VARCHAR(64)")
    private String name;

    @Column(name="provider", columnDefinition = "VARCHAR(64)")
    private String provider;

    @Column(name="pricture_url")
    private String pictureUrl;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private Boolean isActive;

    @Column(name = "is_staff", columnDefinition = "TINYINT(1)")
    private Boolean isStaff;

    @Column(name = "is_superuser", columnDefinition = "TINYINT(1)")
    private Boolean isSuperuser;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Trip> trips = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostBookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();
}

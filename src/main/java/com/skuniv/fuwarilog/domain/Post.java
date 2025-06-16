package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name= "post")
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "diarylist_id")
    private DiaryList diaryList;

    @Column(name = "likes_count", nullable = false)
    @ColumnDefault("0")
    private int likesCount;

    @Column(name = "like_state", nullable = false)
    @ColumnDefault("0")
    private boolean likeState;

    @Column(name = "bookmark_state", nullable = false)
    @ColumnDefault("0")
    private boolean bookmarkState;

    @Column(name = "watch_count", nullable = false)
    @ColumnDefault("0")
    private int watchCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostBookmark> postBookmarks = new ArrayList<>();

}

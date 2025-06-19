package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Entity
@Table(name = "post_view", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "postId", "viewDate"}),
        @UniqueConstraint(columnNames = {"ipAddress", "postId", "viewDate"})
})
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // 비회원은 null

    @Column(name = "ip_address")
    private String ipAddress; // 비회원 식별자 대체

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "view_date")
    private LocalDate viewDate;

}

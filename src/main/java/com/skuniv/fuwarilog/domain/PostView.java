package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Entity
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // 비회원은 null

    private String ipAddress; // 비회원 식별자 대체

    private Long postId;

    private LocalDate viewDate;

}

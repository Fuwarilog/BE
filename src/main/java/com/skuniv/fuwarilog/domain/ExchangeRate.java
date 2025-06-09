package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Table(name= "exchange_rate")
public class ExchangeRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="exchangerate_id")
    private long id;

    @Column(name="cur_unit")
    private String curUnit;

    @Column(name="cur_nm")
    private String curNm;

    @Column(name = "deal_bas_r")
    private Double dealBasR;

    @Column(name="timestamp")
    private LocalDate timestamp;
}
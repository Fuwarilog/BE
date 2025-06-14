package com.skuniv.fuwarilog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name= "exchange_rate")
public class ExchangeRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="exchangerate_id")
    private long id;

    @Column(name="base_currency")
    private String baseCurrency;

    @Column(name="rate_value")
    private double rateValue;

    @Column(name="timestamp")
    private LocalDateTime timestamp;

    @Column(name="percent_change")
    private double percentChange;
}

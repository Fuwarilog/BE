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
public class ExchangeRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="exchangerate_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="currency_id")
    private Currency currency;

    @Column(name="base_currency")
    private String baseCurrency;

    @Column(name="rate_value")
    private double rateValue;

    @Column(name="timestamp")
    private LocalDateTime timestamp;

    @Column(name="percent_change")
    private double percentChange;
}

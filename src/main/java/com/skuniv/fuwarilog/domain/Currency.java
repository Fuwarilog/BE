package com.skuniv.fuwarilog.domain;

import com.skuniv.fuwarilog.domain.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
public class Currency extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="prediction_id")
    private long id;

    @Column(name="currency_code")
    private CurrencyCode code;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    private List<ExchangeRate> exchangeRates = new ArrayList<>();

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    private List<CurrencyPrediction> currencyPredictions = new ArrayList<>();
}

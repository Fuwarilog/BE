//package com.skuniv.fuwarilog.kafka;
//
//import com.skuniv.fuwarilog.dto.ExchangeRate.ExchangeRateResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class EximApiFallback implements EximApiFeignService {
//
//    @Override
//    public List<ExchangeRateResponse.ExchangeRateDTO> findList() {
//        throw new RuntimeException("API 호출 문제 발생");
//    }
//}

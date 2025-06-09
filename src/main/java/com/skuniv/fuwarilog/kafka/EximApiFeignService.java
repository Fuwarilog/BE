//package com.skuniv.fuwarilog.kafka;
//
//import com.skuniv.fuwarilog.domain.ExchangeRate;
//import com.skuniv.fuwarilog.dto.ExchangeRate.ExchangeRateRequest;
//import com.skuniv.fuwarilog.dto.ExchangeRate.ExchangeRateResponse;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//
//
//@FeignClient(name = "koreaAPI", url = "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?data=AP01", fallback = EximApiFallback.class)
//public interface EximApiFeignService {
//
//    @GetMapping
//    List<ExchangeRateResponse.ExchangeRateDTO> findList(
//            @RequestParam("authkey") String authkey,
//            @RequestParam("searchdate") String searchdate
//    );
//}

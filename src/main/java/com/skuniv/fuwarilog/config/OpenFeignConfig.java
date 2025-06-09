//package com.skuniv.fuwarilog.config;
//
//import feign.Retryer;
//import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableFeignClients("com.skuniv.fuwarilog.kafka")
//public class OpenFeignConfig {
//
//    @Bean
//    public Retryer retryer() {
//        return new Retryer.Default(100, 1000, 5);
//    }
//}

package com.skuniv.fuwarilog.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        HttpClient httpClient = HttpClientBuilder.create()
                .disableRedirectHandling()
                .build();

        ClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return new RestTemplate(requestFactory);
    }
}

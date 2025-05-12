package com.skuniv.fuwarilog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FuwarilogApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuwarilogApplication.class, args);
    }

}

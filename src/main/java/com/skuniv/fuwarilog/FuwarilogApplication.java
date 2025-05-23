package com.skuniv.fuwarilog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
@EnableMongoRepositories
@EnableScheduling
public class FuwarilogApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuwarilogApplication.class, args);
    }

}

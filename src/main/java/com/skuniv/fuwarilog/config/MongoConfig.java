package com.skuniv.fuwarilog.config;

import com.skuniv.fuwarilog.config.converter.MongoReadConverter;
import com.skuniv.fuwarilog.config.converter.MongoWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;

import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public CustomConversions customConversions(
            MongoWriteConverter mongoWriteConverter,
            MongoReadConverter mongoReadConverter
    ) {
        return new CustomConversions(CustomConversions.StoreConversions.NONE,
                List.of(mongoWriteConverter, mongoReadConverter));
    }
}

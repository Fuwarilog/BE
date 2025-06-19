package com.skuniv.fuwarilog.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
@ReadingConverter
public class MongoReadConverter implements Converter<Date, LocalDate> {
    @Override
    public LocalDate convert(Date source) {
        return source.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
    }
}
package com.skuniv.fuwarilog.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
@WritingConverter
public class MongoWriteConverter implements Converter<LocalDate, Date> {
    @Override
    public Date convert(LocalDate source) {
        return Date.from(source.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant());
    }
}

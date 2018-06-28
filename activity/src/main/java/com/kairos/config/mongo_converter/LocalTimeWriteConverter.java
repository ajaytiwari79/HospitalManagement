package com.kairos.config.mongo_converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDate;
import java.time.LocalTime;
@WritingConverter
public class LocalTimeWriteConverter implements Converter<LocalTime,String> {
    @Override
    public String convert(LocalTime source) {
        return source.toString();
    }
}

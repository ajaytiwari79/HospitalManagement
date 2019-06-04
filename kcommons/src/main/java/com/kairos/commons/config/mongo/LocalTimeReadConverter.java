package com.kairos.commons.config.mongo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalTime;

@ReadingConverter
public class LocalTimeReadConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        return LocalTime.parse(source);
    }
}

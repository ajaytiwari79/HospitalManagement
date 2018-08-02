package com.kairos.scheduler.config.mongo.mongo_converter;

import com.kairos.util.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDateTime;
import java.util.Date;

@ReadingConverter
public class LocalDateTimeReadConverter implements Converter<Date, LocalDateTime> {
    @Override
    public LocalDateTime convert(Date source) {
        //default, ISO_LOCAL_DATE
        return DateUtils.asLocalDateTime(source);

    }
}

package com.kairos.activity.config.mongo_converter;

import com.kairos.activity.util.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDateTime;
import java.util.Date;
@WritingConverter
public class LocalDateTimeWriteConverter implements Converter<LocalDateTime,Date> {
    @Override
    public Date convert(LocalDateTime source) {
        return DateUtils.asDate(source);
    }
}

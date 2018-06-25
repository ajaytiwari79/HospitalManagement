package com.kairos.activity.config.mongo_converter;

import com.kairos.activity.util.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDate;
import java.util.Date;

@WritingConverter
public class LocalDateToStringWriteConverter implements Converter<LocalDate,Date> {
    @Override
    public Date convert(LocalDate source) {
        return DateUtils.asDate(source);
    }
}
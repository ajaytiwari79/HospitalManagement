package com.kairos.scheduler.config.mongo.mongo_converter;

import com.kairos.commons.utils.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDate;
import java.util.Date;

@WritingConverter
public class LocalDateWriteConverter implements Converter<LocalDate,Date> {
    @Override
    public Date convert(LocalDate source) {
        return DateUtils.asDate(source);
    }
}

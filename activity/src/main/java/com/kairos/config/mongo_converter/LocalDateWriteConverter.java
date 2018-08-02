package com.kairos.config.mongo_converter;

import com.kairos.util.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDate;
import java.util.Date;

@WritingConverter
public class LocalDateWriteConverter implements Converter<LocalDate,Date> {
    @Override
    public Date convert(LocalDate source) {
        return DateUtils.getDateFromLocalDate(source);
    }
}

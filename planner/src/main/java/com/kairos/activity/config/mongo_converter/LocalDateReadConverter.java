package com.kairos.activity.config.mongo_converter;
import com.kairos.commons.utils.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDate;
import java.util.Date;

@ReadingConverter
public class LocalDateReadConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate convert(Date source) {
        //default, ISO_LOCAL_DATE
        return DateUtils.asLocalDate(source);

    }
}

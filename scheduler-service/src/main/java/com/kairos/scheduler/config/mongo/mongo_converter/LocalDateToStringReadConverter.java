package com.kairos.scheduler.config.mongo.mongo_converter;

import com.kairos.util.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDate;

@ReadingConverter
public class LocalDateToStringReadConverter  implements Converter<String, LocalDate>  {

    @Override
    public LocalDate convert(String source){
        //default, ISO_LOCAL_DATE
        return DateUtils.asLocalDate(source);

    }
}

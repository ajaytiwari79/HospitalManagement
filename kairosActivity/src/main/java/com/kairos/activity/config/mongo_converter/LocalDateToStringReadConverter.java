package com.kairos.activity.config.mongo_converter;

import com.kairos.activity.util.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
@ReadingConverter
public class LocalDateToStringReadConverter  implements Converter<String, LocalDate>  {

    @Override
    public LocalDate convert(String source){
        //default, ISO_LOCAL_DATE
        return DateUtils.asLocalDate(source);

    }
}

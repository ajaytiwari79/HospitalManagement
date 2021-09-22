package com.kairos.config.mongo_converter;

import com.kairos.commons.utils.DateUtils;
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

package com.kairos.config.mongo_converter;

import org.springframework.core.convert.converter.Converter;

import java.time.ZonedDateTime;
import java.util.Date;

import static com.kairos.commons.utils.DateUtils.asDate;

/**
 * Created by pradeep
 * Created at 2/7/19
 **/

public class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {
    @Override
    public Date convert(ZonedDateTime source) {
        return asDate(source);
    }
}

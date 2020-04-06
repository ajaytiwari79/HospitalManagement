package com.kairos.config.mongo_converter;

import com.kairos.commons.utils.DateUtils;
import org.springframework.core.convert.converter.Converter;

import java.time.ZonedDateTime;
import java.util.Date;

import static com.kairos.commons.utils.DateUtils.asZonedDateTime;

/**
 * Created by pradeep
 * Created at 2/7/19
 **/

public class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {
    @Override
    public ZonedDateTime convert(Date source) {
        return DateUtils.asZonedDateTime(source);
    }
}

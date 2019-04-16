package com.kairos.config.neo4j.converter;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oodles on 5/6/18.
 */
public class LocalDateListConverter implements AttributeConverter<List<LocalDate>, List<Long>> {

    @Override
    public List<Long> toGraphProperty(List<LocalDate> localDates) {
        List<Long> dateAsLong = new ArrayList<>();
        if (localDates!=null && !localDates.isEmpty()){
            localDates.forEach(localDate ->{
                dateAsLong.add(localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
            });
        }
        return dateAsLong;
    }

    @Override
    public List<LocalDate> toEntityAttribute(List<Long> dateLongs) {
        List<LocalDate> localDateList = new ArrayList<>();
        if (dateLongs!=null && !dateLongs.isEmpty()){
            dateLongs.forEach(dateLong -> {
                localDateList.add(Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDate());
            });
        }
        return localDateList;
    }

}

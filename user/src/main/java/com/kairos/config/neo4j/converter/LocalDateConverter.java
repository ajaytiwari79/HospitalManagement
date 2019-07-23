package com.kairos.config.neo4j.converter;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.time.*;
import java.util.Optional;

public class LocalDateConverter implements AttributeConverter<LocalDate, Long> {
    @Override
    public Long toGraphProperty(LocalDate value) {
        if (value == null) return null;
        return value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    @Override
    public LocalDate toEntityAttribute(Long dateLong) {
        LocalDate date = null;
        if (Optional.ofNullable(dateLong).isPresent()) {
            date = Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return date;
    }
}

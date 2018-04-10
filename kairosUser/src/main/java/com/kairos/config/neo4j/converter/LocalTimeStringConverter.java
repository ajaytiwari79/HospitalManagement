package com.kairos.config.neo4j.converter;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.time.LocalTime;

public class LocalTimeStringConverter implements AttributeConverter<LocalTime, String> {
    @Override
    public String toGraphProperty(LocalTime value) {
        if (value == null) return null;
        return value.toString();
    }

    @Override
    public LocalTime toEntityAttribute(String value) {
        if (value == null) return null;
        return LocalTime.parse(value);
    }
}

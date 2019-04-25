package com.kairos.config.neo4j.converter;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author pradeep
 * @date - 11/6/18
 */

public class LocalTimeConverter implements AttributeConverter<LocalTime, String> {
    DateTimeFormatter FORMATTER = ofPattern("HH:mm");

    @Override
    public String toGraphProperty(LocalTime value) {
        return value!=null ? value.toString() : null;
    }

    @Override
    public LocalTime toEntityAttribute(String value) {
        return value!=null ? LocalTime.parse(value,FORMATTER) : null;
    }
}

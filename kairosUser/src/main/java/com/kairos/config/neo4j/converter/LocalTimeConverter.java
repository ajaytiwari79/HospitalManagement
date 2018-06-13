package com.kairos.config.neo4j.converter;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author pradeep
 * @date - 11/6/18
 */

public class LocalTimeConverter implements AttributeConverter<LocalDate, String> {
    DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");

    @Override
    public String toGraphProperty(LocalDate value) {
        return value!=null ? value.toString() : null;
    }

    @Override
    public LocalDate toEntityAttribute(String value) {
        return value!=null ? LocalDate.parse(value,FORMATTER) : null;
    }
}

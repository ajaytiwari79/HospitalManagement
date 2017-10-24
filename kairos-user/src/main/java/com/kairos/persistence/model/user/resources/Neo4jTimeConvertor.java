package com.kairos.persistence.model.user.resources;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Created by prabjot on 16/10/17.
 */
public class Neo4jTimeConvertor implements AttributeConverter<LocalTime,Long> {

    @Override
    public Long toGraphProperty(LocalTime value) {
        if(Optional.ofNullable(value).isPresent()){
            return value.toNanoOfDay();
        }
        return null;
    }

    @Override
    public LocalTime toEntityAttribute(Long value) {
        if(Optional.ofNullable(value).isPresent()){
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault());
            return LocalTime.of(date.getHour(),date.getSecond());
        }
        return null;
    }
}

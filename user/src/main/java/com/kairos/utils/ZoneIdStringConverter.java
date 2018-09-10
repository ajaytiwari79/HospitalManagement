package com.kairos.utils;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.time.ZoneId;

/**
 * Created by oodles on 14/12/17.
 */

    public class ZoneIdStringConverter implements AttributeConverter<ZoneId, String> {

    @Override
    public String toGraphProperty(ZoneId value) {
        return (value == null) ? null : value.toString();
    }

    @Override
    public ZoneId toEntityAttribute(String value) {
        return (value == null) ? null : ZoneId.of(value);
    }
   }


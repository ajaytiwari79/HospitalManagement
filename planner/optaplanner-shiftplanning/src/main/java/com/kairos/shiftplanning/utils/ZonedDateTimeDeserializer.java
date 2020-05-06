package com.kairos.shiftplanning.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.kairos.commons.utils.ObjectUtils.isNull;


public class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {

        private DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS[XXX][X]");


    public ZonedDateTimeDeserializer() {
        this(null);
    }
    public ZonedDateTimeDeserializer(Class<ZonedDateTime> t) {
        super(t);
    }


    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String text = p.getText();
        ZonedDateTime zonedDateTime = getZonedDateTime(text,formatter);
        if(isNull(zonedDateTime)){
            zonedDateTime = getZonedDateTime(text,DateTimeFormatter.ISO_ZONED_DATE_TIME);
        }
        return zonedDateTime;
    }

    private ZonedDateTime getZonedDateTime(String text, DateTimeFormatter formatter) {
        try {
            return ZonedDateTime.parse(text, formatter);
        }catch (RuntimeException e){

        }
        return null;
    }
}

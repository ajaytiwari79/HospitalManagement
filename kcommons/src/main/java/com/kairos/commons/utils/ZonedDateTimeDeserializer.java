package com.kairos.commons.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer extends JSR310DateTimeDeserializerBase<ZonedDateTime> {
    protected ZonedDateTimeDeserializer(Class<ZonedDateTime> supportedType, DateTimeFormatter f) {
        super(supportedType, f);
    }

    @Override
    protected JsonDeserializer<ZonedDateTime> withDateFormat(DateTimeFormatter dtf) {
        return null;
    }

    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return null;
    }
}

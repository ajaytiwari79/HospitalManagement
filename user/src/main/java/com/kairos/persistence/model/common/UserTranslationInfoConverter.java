package com.kairos.persistence.model.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By Pavan on 19/11/20
 **/
public class UserTranslationInfoConverter implements AttributeConverter<Map, String> {

        private final static ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String toGraphProperty(Map value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Map toEntityAttribute(String value) {
        try {
            return objectMapper.readValue(value, Map.class);
        } catch (Exception ex) {
            return new HashMap();
        }
    }
}

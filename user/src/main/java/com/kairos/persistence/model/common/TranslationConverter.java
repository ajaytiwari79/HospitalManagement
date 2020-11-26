package com.kairos.persistence.model.common;


import com.kairos.commons.utils.ObjectMapperUtils;
import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By Pavan on 19/11/20
 **/
public class TranslationConverter implements AttributeConverter<Map, String> {

    @Override
    public String toGraphProperty(Map value) {
        try {
            return ObjectMapperUtils.mapper.writeValueAsString(value);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Map toEntityAttribute(String value) {
        try {
            return ObjectMapperUtils.mapper.readValue(value, Map.class);
        } catch (Exception ex) {
            return new HashMap();
        }
    }
}

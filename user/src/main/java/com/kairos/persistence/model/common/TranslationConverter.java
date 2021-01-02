package com.kairos.persistence.model.common;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.TranslationInfo;
import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By Pavan on 19/11/20
 **/
public class TranslationConverter implements AttributeConverter<Map<String,TranslationInfo>, String> {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    @Override
    public String toGraphProperty(Map value) {
        try {
            return ObjectMapperUtils.mapper.writeValueAsString(value);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Map<String,TranslationInfo> toEntityAttribute(String value) {
        try {
            Map<String,Object> translatedMap = ObjectMapperUtils.mapper.readValue(value, Map.class);
            Map<String, TranslationInfo> infoMap = new HashMap<>();
            translatedMap.forEach((k, v) -> infoMap.put(k, new TranslationInfo(((Map)v).get(NAME).toString(),((Map)v).get(DESCRIPTION).toString())));
            return infoMap;
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }
}

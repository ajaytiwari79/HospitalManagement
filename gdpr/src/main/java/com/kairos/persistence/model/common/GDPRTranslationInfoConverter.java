package com.kairos.persistence.model.common;


import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By G.P.Ranjan on 4/3/20
 **/
public class GDPRTranslationInfoConverter implements AttributeConverter<Map, String> {

        private final static ObjectMapper objectMapper = new ObjectMapper();

        @Override
        @NotNull
        public String convertToDatabaseColumn(@NotNull Map myCustomObject) {
            try {
                return objectMapper.writeValueAsString(myCustomObject);
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        @NotNull
        public Map convertToEntityAttribute(@NotNull String databaseDataAsJSONString) {
            try {
                return objectMapper.readValue(databaseDataAsJSONString, Map.class);
            } catch (Exception ex) {
                return new HashMap();
            }
        }
}

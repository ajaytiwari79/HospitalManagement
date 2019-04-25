package com.kairos.persistence.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.Lists;
import com.kairos.persistence.model.access_permission.Tab;

import java.io.IOException;
import java.util.List;

/**
 * Created by prabjot on 5/1/17.
 */
public class XmlDeserializer extends StdDeserializer<Tab> {

    protected XmlDeserializer() {
        super(Tab.class);
    }

    @Override
    public Tab deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Invalid token, expected START_OBJECT");
        }

        String id = null;
        boolean isModule = false;
        String name = null;
        final List<Tab> subPages = Lists.newArrayList();

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            final String key = jp.getCurrentName();
            jp.nextToken();

            if ("moduleId".equals(key)) {
                id = jp.readValueAs(String.class);
            } else if("isModule".equals(key)){
                isModule = jp.readValueAs(Boolean.class);
            }else if("name".equals(key)){
                name = jp.readValueAs(String.class);
            } else if ("page".equals(key)) {
                final Tab child = jp.readValueAs(Tab.class);
                if (child != null) {
                    subPages.add(child);
                }
            }
        }

        jp.close();

        return new Tab(id, name,isModule,subPages);
    }
}

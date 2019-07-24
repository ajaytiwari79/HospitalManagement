package com.kairos.serializers;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import java.io.IOException;
import java.util.Date;

/**
 * Created by prabjot on 7/6/17.
 */
public class MongoDateMapper {


    public static ObjectMapper objectMapper(){

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null, null, null));
        testModule.addSerializer(Date.class, new StdSerializer<Date>(Date.class) {

            @Override
            public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                TokenBuffer buffer = (TokenBuffer) jgen;
                ObjectCodec codec = buffer.getCodec();
                buffer.setCodec(null);

                buffer.writeObject(value);

                buffer.setCodec(codec);
            }
        });

        objectMapper.registerModule(testModule);
        return objectMapper;
    }
}

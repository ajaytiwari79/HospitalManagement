package com.kairos.persistence.model.deserializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.kairos.persistence.model.auth.User;

import java.io.IOException;

/**
 * Custom Serializer for User domain
 */
public class UserSerializer extends StdSerializer<User>{

    /**
     * default constructor
     */
    public UserSerializer() {
        this(null);
    }

    /**
     * Constructor for POJO
     * @param t
     */
    public UserSerializer(Class<User> t) {
        super(t);
    }

    /**
     * Serialize method, you can add fields as per requirement
     * @param value
     * @param jgen
     * @param provider
     * @throws IOException
     * @throws JsonGenerationException
     */
    @Override
    public void serialize(User value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", value.getId());
        jgen.writeStringField("name", value.getFirstName());
        jgen.writeStringField("userName", value.getUserName());
        jgen.writeStringField("accessToken", value.getAccessToken());
        jgen.writeArrayFieldStart("roles");
        jgen.writeEndArray();

        jgen.writeStringField("email", value.getEmail());
        jgen.writeNumberField("age", value.getAge());
        jgen.writeEndObject();

    }
}

package com.kairos.persistence.model.deserializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.kairos.persistence.model.organization.Organization;

import java.io.IOException;

/**
 * Custom Serializer for Organization domain
 */
public class OrganizationSerializer extends StdSerializer<Organization>{

    /**
     * default constructor
     */
    public OrganizationSerializer() {
        this(null);
    }

    /**
     * Constructor for POJO
     * @param t
     */
    public OrganizationSerializer(Class<Organization> t) {
        super(t);
    }

    /**
     *
     * @param value
     * @param jgen
     * @param provider
     * @throws IOException
     * @throws JsonGenerationException
     */
    @Override
    public void serialize(Organization value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", value.getId());
        jgen.writeStringField("name", value.getName());
        jgen.writeStringField("email", value.getEmail());
        jgen.writeObject(value.getContactDetail());
//        jgen.writeStringField("address", value.getContactAddress());
        jgen.writeEndObject();


    }
}

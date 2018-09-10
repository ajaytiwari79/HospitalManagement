package com.kairos.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Utility class to support JSON operations.
 *
 */
public class JsonUtils {
  private static ObjectMapper jsonMapper = new ObjectMapper();

  /**
   * Private constructor.
   */
  private JsonUtils() {}

  /**
   * Converting object to JSON string. If errors appears throw MeshinException runtime exception.
   *
   * @param object The object to convert.
   * @return The JSON string representation.
   * @throws IOException IO issues
   * @throws JsonMarshallingException failure to generate JSON
   */
  public static String toJSON(Object object) {
    final Writer json = new StringWriter();
    try {
      jsonMapper.writeValue(json, object);
      return json.toString();
    } catch (final Exception e) {
      throw new JsonMarshallingException(e.toString(), e);
    }
  }

  /**
   * Convert string representation to object. If errors appears throw Exception runtime exception.
   *
   * @param value The JSON string.
   * @param klazz The class to convert.
   * @return The Object of the given class.
   */
  public static <T> T toObject(String value, Class<T> klazz) {
    try {
      return jsonMapper.readValue(value, klazz);
    } catch (final JsonGenerationException e) {
      throw new JsonMarshallingException(e.toString() + " source: " + value, e);
    } catch (final JsonMappingException e) {
      throw new JsonMarshallingException(e.toString() + " source: " + value, e);
    } catch (IOException e) {
      throw new JsonMarshallingException(e.toString() + " source: " + value, e);
    }
  }

  /**
   * @param value json string
   * @param typeReference class type reference
   * @param <T> type
   * @return deserialized T object
   */
  @SuppressWarnings("unchecked")
  public static <T> T toObject(String value, final TypeReference<T> typeReference) {
    try {
      return (T) jsonMapper.readValue(value, typeReference);
    } catch (final Exception e) {
      throw new JsonMarshallingException("Error creating object", e);
    }
  }

  public static JsonNode toJsonNode(String value) {
    try {
      return jsonMapper.readTree(value);
    } catch (final Exception e) {
      throw new JsonMarshallingException("Error creating JsonNode object", e);
    }
  }

  /**
   * @param value json string
   * @param collectionType class describing how to deserialize collection of objects
   * @param <T> type
   * @return deserialized T object
   */
  @SuppressWarnings("unchecked")
  public static <T> T toObject(String value, final CollectionType collectionType)
      throws IOException {
    try {
      return (T) jsonMapper.readValue(value, collectionType);
    } catch (final JsonGenerationException e) {
      throw new JsonMarshallingException(e.toString() + " source: " + value, e);
    } catch (final JsonMappingException e) {
      throw new JsonMarshallingException(e.toString() + " source: " + value, e);
    }
  }

  public static ObjectMapper getJsonMapper() {
    return jsonMapper;
  }


  /**
   * Exception to handle json marshalling and unmarshalling issues.
   *
   * @author pani
   *
   */
  private static class JsonMarshallingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JsonMarshallingException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}

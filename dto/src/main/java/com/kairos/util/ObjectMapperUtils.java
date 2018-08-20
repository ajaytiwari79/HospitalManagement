package com.kairos.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author pradeep
 * @date - 26/4/18
 */

public class ObjectMapperUtils {
    public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");

    public static <T,E extends Object> List<E> copyProperties(List<T> objects1,Class className) {
        List<E> objects = new ArrayList<>();
        for (int i = 0; i < objects1.size(); i++) {
            try {
                E e = (E) className.newInstance();
                PropertyUtils.copyProperties(e,objects1.get(i));
                objects.add(e);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |InstantiationException e) {
            }
        }
        return objects;
    }

    /*private static <E> List<E> assignBlankObject(int size, List<E> objects) {
        List<E> objects = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            try {
                objects.add(t.getClass().newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return objects;
    }*/

    public static <T> List<T> copyList(Class<T> klazz) {
        List<T> list = new ArrayList<>();
        Object actuallyT = new Object();
        list.add(klazz.cast(actuallyT));
        try {
            list.add(klazz.getConstructor().newInstance()); // If default constructor
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static <T extends Object,E extends Object> List<E> copyPropertiesOfListByMapper(List<T> objects, Class className) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(FORMATTER));
        mapper.registerModule(javaTimeModule);
        try {
            return mapper.readValue(mapper.writeValueAsString(objects), mapper.getTypeFactory().constructCollectionType(
                    List.class, className));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static <E extends Object,T extends Object> T copyPropertiesByMapper(E object,Class<T> valueType,String... fields){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAllExcept(fields);
        FilterProvider filters = new SimpleFilterProvider().addFilter("PropertyFilterMixIn", theFilter);
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(FORMATTER));
        objectMapper.registerModule(javaTimeModule);
        //objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            //writer(filters).
            String json = objectMapper.writeValueAsString(object);
            return objectMapper.readValue(json, valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> String objectToJsonString(T object){
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(FORMATTER));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        //objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T JsonStringToObject(String jsonString,Class<T> valueType){
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(FORMATTER));
        objectMapper.registerModule(javaTimeModule);
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyProperties(Object source,Object destination){
        try {
            PropertyUtils.copyProperties(destination,source);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public  static ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

     public static void copyPropertiesUsingBeanUtils(Object source,Object destination,String ...ignoreProperties) {
              BeanUtils.copyProperties(source,destination,ignoreProperties);
        }


    @JsonFilter("JSON_FILTER")
    class PropertyFilterMixIn {}
}

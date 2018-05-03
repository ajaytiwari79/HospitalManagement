package com.kairos.activity.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 26/4/18
 */

public class ObjectMapperUtils {


    public static <T,E extends Object> List<E> copyProperties(List<T> objects1,Class className) {
        List<E> objects = new ArrayList<>();
        for (int i = 0; i < objects1.size(); i++) {
            try {
                E e = (E) className.newInstance();
                PropertyUtils.copyProperties(objects1.get(i),e);
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

    public static <T extends Object,E extends Object> List<E> copyPropertiesByMapper(List<T> objects1, E Objects) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            return mapper.convertValue(mapper.writeValueAsString(objects1), new TypeReference<List<E>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static <T> T copyPropertiesByMapper(Object object,Class<T> valueType){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(object), valueType);
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


}

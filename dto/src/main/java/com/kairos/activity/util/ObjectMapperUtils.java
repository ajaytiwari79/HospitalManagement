package com.kairos.activity.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.activity.response.dto.ActivityDTO;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author pradeep
 * @date - 26/4/18
 */

public class ObjectMapperUtils {


    /*public static <T> List<T> copyProperties(List<T> objects1, List<Object> objects) {
        List<T> objects = assignBlankObject(objects1.size(), t);
        for (int i = 0; i < objects1.size(); i++) {
            try {
                PropertyUtils.copyProperties(objects1.get(i), objects.get(i));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return objects;
    }*/

    private static <T> List<T> assignBlankObject(int size, List<Object> objects) {
        //List<T> objects = new ArrayList<>(size);
        /*for (int i = 0; i < size; i++) {
            try {
                objects.add(t.getClass().newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }*/
        return null;//objects;
    }

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

    public static <T extends Object> List<T> copyPropertiesByObjectMapper(List<T> objects1, T Object) {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(mapper, new TypeReference<List<T>>() {
        });
    }


}

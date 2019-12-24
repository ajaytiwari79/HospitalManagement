package com.kairos.commons.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.kairos.dto.kpermissions.FieldDTO;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.*;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author pradeep
 * @date - 26/4/18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectMapperUtils {
    public static final DateTimeFormatter LOCALDATE_FORMATTER = ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter LOCALTIME_FORMATTER = ofPattern("HH:mm");
    public static final String ERROR = "error {}";

    private static ObjectMapper mapper;

    private static  final Logger LOGGER = LoggerFactory.getLogger(ObjectMapperUtils.class);


    static {
        mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(LOCALDATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(LOCALDATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(LOCALTIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(LOCALTIME_FORMATTER));
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(javaTimeModule);
    }

    /*public static <T,E extends Object> List<E> copyProperties(List<T> objects1, Class className) {
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
*/

    public static <T,E,F extends Collection> F copyPropertiesOfCollectionByMapper(Collection<T> objects, Class<E> elementClass,Class... type) {
        Class className = getClassByIntance(objects);
        try {
            return mapper.readValue(mapper.writeValueAsString(objects), mapper.getTypeFactory().constructCollectionType(
                    className, elementClass));
        } catch (IOException e) {
            LOGGER.error(ERROR,e);
        }
        return null;
    }

    private static <T> Class getClassByIntance(Collection<T> object,Class... type){
        if(type.length>0){
            return type[0];
        }
        if(object instanceof Set){
            return Set.class;
        }else if (object instanceof List){
            return List.class;
        }
        return Collection.class;
    }



    public static <E extends Object,T extends Object> T copyPropertiesByMapper(E object,Class<T> valueType){
        try {
            String json = mapper.writeValueAsString(object);
            return mapper.readValue(json, valueType);
        } catch (IOException e) {
            LOGGER.error(ERROR,e);
        }
        return null;
    }

    public static <T> String objectToJsonString(T object){
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            LOGGER.error(ERROR,e);
        }
        return null;
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> valueType){
        try {
            return mapper.readValue(jsonString, valueType);
        } catch (IOException e) {
            LOGGER.error(ERROR,e);
        }
        return null;
    }

    public static <E extends Object> List<E> jsonStringToList(String json, Class className) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(
                    List.class, className));
        } catch (IOException e) {
            LOGGER.error(ERROR,e);
        }
        return new ArrayList<>();
    }

    //Todo Please don't use again, pradeep remove this method
    @Deprecated
    public static void copyProperties(Object source,Object destination){
        try {
            PropertyUtils.copyProperties(destination,source);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(ERROR,e);
        }
    }

    public  static ObjectMapper getObjectMapper(){
        return mapper;
    }


}

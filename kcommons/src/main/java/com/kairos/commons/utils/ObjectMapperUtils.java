package com.kairos.commons.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.kairos.dto.kpermissions.KPermissionModelFieldDTO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author pradeep
 * @date - 26/4/18
 */

public class ObjectMapperUtils {
    public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");

    private static ObjectMapper mapper;



    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(FORMATTER));
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

    public static <T,E> List<E> copyPropertiesOfListByMapper(Collection<T> objects, Class className) {
        try {
            return mapper.readValue(mapper.writeValueAsString(objects), mapper.getTypeFactory().constructCollectionType(
                    List.class, className));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Object,E extends Object> Set<E> copyPropertiesOfSetByMapper(Set<T> objects, Class className) {
        try {
            return mapper.readValue(mapper.writeValueAsString(objects), mapper.getTypeFactory().constructCollectionType(
                    Set.class, className));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static <E extends Object,T extends Object> T copyPropertiesByMapper(E object,Class<T> valueType){
        try {
            String json = mapper.writeValueAsString(object);
            return mapper.readValue(json, valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> String objectToJsonString(T object){
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> valueType){
        try {
            return mapper.readValue(jsonString, valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Object,E extends Object> List<E> JsonStringToList(String json, Class className) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(
                    List.class, className));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Todo Please don't use again pradeep remove this method
    @Deprecated
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
        return mapper;
    }

    public static <E extends Object>  E copyObjectSpecificPropertiesByMapper(Object src, Object target, List<KPermissionModelFieldDTO> accessibleFieldsWithModelName, Class baseClass) {
        if (src != null) {
            BeanWrapper targetWrapper = null;
            try {
                BeanWrapper srcWrapper = PropertyAccessorFactory.forBeanPropertyAccess(src);
                if (target == null) {
                    target = src.getClass().newInstance();
                }
                targetWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
                Map<String, Object> subModelObjects = new HashMap<>();
                for (KPermissionModelFieldDTO kPermissionModelFieldDTO : accessibleFieldsWithModelName) {
                    String modelName = kPermissionModelFieldDTO.getModelName();
                    if (!modelName.equalsIgnoreCase(src.getClass().getSimpleName()) && srcWrapper.getPropertyType(modelName) != null && baseClass.isAssignableFrom(srcWrapper.getPropertyType(modelName)) ) {
                        Object validatedObject = copyObjectSpecificPropertiesByMapper(srcWrapper.getPropertyValue(modelName),
                                targetWrapper.getPropertyValue(modelName), kPermissionModelFieldDTO.getModelFields());
                        subModelObjects.put(modelName, validatedObject);
                    } else if (modelName.equalsIgnoreCase(src.getClass().getSimpleName())) {
                        for (String field : kPermissionModelFieldDTO.getModelFields()) {
                            if (subModelObjects.containsKey(field)) {
                                targetWrapper.setPropertyValue(field, subModelObjects.get(field));
                            } else {
                                targetWrapper.setPropertyValue(field, srcWrapper.getPropertyValue(field));
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return (E) targetWrapper.getWrappedInstance();
        }else{
            return null;
        }
    }

    public static <E extends Object>  E copySpecificPropertiesByMapper(Object src, Object target, List<KPermissionModelFieldDTO> accessibleFieldsWithModelName) {
        if (src != null) {
            BeanWrapper targetWrapper = null;
            try {
                BeanWrapper srcWrapper = PropertyAccessorFactory.forBeanPropertyAccess(src);
                if (target == null) {
                    target = src.getClass().newInstance();
                }
                targetWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
                for (KPermissionModelFieldDTO kPermissionModelFieldDTO : accessibleFieldsWithModelName) {
                    for (String field : kPermissionModelFieldDTO.getModelFields()) {
                        if(targetWrapper.isWritableProperty(field) && srcWrapper.isReadableProperty(field)) {
                            targetWrapper.setPropertyValue(field, srcWrapper.getPropertyValue(field));
                        }
                        }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return (E) targetWrapper.getWrappedInstance();
        }else{
            return null;
        }
    }

    public static <E extends Object>  E copyObjectSpecificPropertiesByMapper(Object src, Object target, List<String> accessibleFieldNames) {
        BeanWrapper targetWrapper = null;
        if (src != null) {
            try {
                BeanWrapper srcWrapper = PropertyAccessorFactory.forBeanPropertyAccess(src);
                if (target == null) {
                    target = src.getClass().newInstance();
                }
                targetWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
                for (String prop : accessibleFieldNames) {
                    targetWrapper.setPropertyValue(prop, srcWrapper.getPropertyValue(prop));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        return (E)targetWrapper.getWrappedInstance();
        }else{
            return null;
        }
    }

}

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
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
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

    public static <T,E,F extends Collection> F copyPropertiesOfCollectionByMapper(Collection<T> objects, Class<E> elementClass) {
        Class className = getClassByIntance(objects);
        try {
            return mapper.readValue(mapper.writeValueAsString(objects), mapper.getTypeFactory().constructCollectionType(
                    className, elementClass));
        } catch (IOException e) {
            LOGGER.error(ERROR,e);
        }
        return null;
    }

    private static <T> Class getClassByIntance(Collection<T> object){
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

    /*public static <E extends Object>  E copyObjectSpecificPropertiesByMapper(Object src, Object target, List<KPermissionModelFieldDTO> accessibleFieldsWithModelName, Class baseClass) {
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
                    mapModelDataByPermission(src, baseClass, targetWrapper, srcWrapper, subModelObjects, kPermissionModelFieldDTO);
                }
                return (E) targetWrapper.getWrappedInstance();
            } catch (Exception ex) {
                LOGGER.error(ERROR,ex);
            }
        }else{
            return null;
        }
        return null;
    }*/

    /*private static void mapModelDataByPermission(Object src, Class baseClass, BeanWrapper targetWrapper, BeanWrapper srcWrapper, Map<String, Object> subModelObjects, KPermissionModelFieldDTO kPermissionModelFieldDTO) {
        String modelName = kPermissionModelFieldDTO.getModelName();
        if (!modelName.equalsIgnoreCase(src.getClass().getSimpleName()) && srcWrapper.getPropertyType(modelName) != null && baseClass.isAssignableFrom(srcWrapper.getPropertyType(modelName)) ) {
            Object validatedObject = copyObjectSpecificPropertiesByMapper(srcWrapper.getPropertyValue(modelName),
                    targetWrapper.getPropertyValue(modelName), kPermissionModelFieldDTO.getFieldPermissions());
            subModelObjects.put(modelName, validatedObject);
        } else if (modelName.equalsIgnoreCase(src.getClass().getSimpleName())) {
            for (String field : kPermissionModelFieldDTO.getFieldPermissions()) {
                if (subModelObjects.containsKey(field)) {
                    targetWrapper.setPropertyValue(field, subModelObjects.get(field));
                } else {
                    targetWrapper.setPropertyValue(field, srcWrapper.getPropertyValue(field));
                }
            }
        }
    }*/

    public static <E>  E copySpecificPropertiesByMapper(E src, E target, ModelDTO modelDTO) {
        if (src != null) {
            BeanWrapper targetWrapper = null;
            try {
                if (isNull(target)) {
                    target = (E)src.getClass().newInstance();
                }
               // updateObjectByPermission(modelDTO,src,target);
                BeanWrapper srcWrapper = PropertyAccessorFactory.forBeanPropertyAccess(src);
                targetWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
                updatePropertyByPermission(modelDTO, targetWrapper, srcWrapper,"");
                return (E) srcWrapper.getWrappedInstance();
            } catch (Exception ex) {
                LOGGER.error(ERROR,ex);
            }
        }else{
            return null;
        }
        return null;
    }

    private static <E> void updateObjectByPermission(ModelDTO modelDTO, E src, E target) {
        try {
            if(!(src instanceof Collection)){
                if (isNull(target)) {
                    target = (E) src.getClass().newInstance();
                }
                updatePermission(modelDTO, src, target);
            }else {
                /*for (E o : ((Collection) src)) {
                    updateObjectByPermission()
                }*/
            }
        } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static <E> void updatePermission(ModelDTO modelDTO, E src, E target) throws NoSuchFieldException, IllegalAccessException {
        for (FieldDTO field : modelDTO.getFieldPermissions()) {
            updateFieldValueByPermission(src, target, field);
            if (isCollectionNotEmpty(modelDTO.getSubModelPermissions())) {
                for (ModelDTO subModelPermission : modelDTO.getSubModelPermissions()) {
                    updateObjectByPermission(subModelPermission, src.getClass().getDeclaredField(subModelPermission.getModelName()), target.getClass().getDeclaredField(subModelPermission.getModelName()));

                }
            }
        }
    }

    private static <E> void updateFieldValueByPermission(E src, E target, FieldDTO field) throws NoSuchFieldException, IllegalAccessException {
        if (!field.getPermissions().contains(FieldLevelPermission.WRITE)) {
            Field srcField = src.getClass().getDeclaredField(field.getFieldName());
            srcField.setAccessible(true);
            srcField.get(src);
            Field targetField = src.getClass().getDeclaredField(field.getFieldName());
            targetField.setAccessible(true);
            srcField.set(src, targetField.get(target));
        }
    }


    private static void updatePropertyByPermission(ModelDTO modelDTO, BeanWrapper targetWrapper, BeanWrapper srcWrapper,String subFieldName) {
        for (FieldDTO field : modelDTO.getFieldPermissions()) {
            if (!field.getPermissions().contains(FieldLevelPermission.WRITE) && targetWrapper.isReadableProperty(subFieldName+field.getFieldName()) && srcWrapper.isWritableProperty(subFieldName+field.getFieldName())) {
                srcWrapper.setPropertyValue(subFieldName+field.getFieldName(), targetWrapper.getPropertyValue(subFieldName+field.getFieldName()));
            }
        }
        if(isCollectionNotEmpty(modelDTO.getSubModelPermissions())){
            for (ModelDTO subModelPermission : modelDTO.getSubModelPermissions()) {
                updatePropertyByPermission(subModelPermission, targetWrapper, srcWrapper,subModelPermission.getModelName()+".");

            }
        }
    }

    /*private static void setPropertyByPermission(BeanWrapper targetWrapper, BeanWrapper srcWrapper, KPermissionModelFieldDTO kPermissionModelFieldDTO) {
        for (String field : kPermissionModelFieldDTO.getModelFields()) {
            if(targetWrapper.isWritableProperty(field) && srcWrapper.isReadableProperty(field)) {
                targetWrapper.setPropertyValue(field, srcWrapper.getPropertyValue(field));
            }
        }
    }*/

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
                return (E)targetWrapper.getWrappedInstance();
            } catch (Exception ex) {
                LOGGER.error(ERROR,ex);
            }
        }else{
            return null;
        }
        return null;
    }

}

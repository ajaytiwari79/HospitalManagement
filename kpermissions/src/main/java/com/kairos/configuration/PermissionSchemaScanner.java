package com.kairos.configuration;

import com.kairos.annotations.*;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.constants.ApplicationConstants.*;


public class PermissionSchemaScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionSchemaScanner.class);

    public List<Map<String, Object>> createPermissionSchema(String domainPackagePath) {
        List<Map<String, Object>> modelData = new ArrayList<>();
        try {
            Reflections reflections = new Reflections(ClasspathHelper.forPackage(domainPackagePath));
            reflections.getTypesAnnotatedWith(KPermissionModel.class)
                    .forEach(permissionClass -> {
                        Map<String, Object> modelMetaData = new HashMap<>();
                        Set<Map<String, String>> fields = new HashSet<>();
                        Arrays.stream(permissionClass.getDeclaredFields())
                                .filter(entityField -> entityField.isAnnotationPresent(KPermissionField.class))
                                .forEach(permissionEntityField -> {
                                    Map<String, String> fieldsData = new HashMap<>();
                                    fieldsData.put(FIELD_NAME, permissionEntityField.getName());
                                    fields.add(fieldsData);
                                });
                        List<Map<String, Object>> subModelData = findSubModelData(permissionClass);
                        getRelationShipTypeModelData(permissionClass, fields, reflections);
                        modelMetaData.put(MODEL_NAME, permissionClass.getSimpleName());
                        modelMetaData.put(MODEL_CLASS, permissionClass.toString());
                        modelMetaData.put(FIELDS, fields);
                        modelMetaData.put(SUB_MODEL, subModelData);
                        modelData.add(modelMetaData);
                    });
            LOGGER.info("model== {}",modelData);

        } catch (Exception ex) {
            LOGGER.error("ERROR in identifying permission models====== {}",ex.getMessage());
        }
        return modelData;
    }

    private void getRelationShipTypeModelData(Class permissionClass, Set<Map<String, String>> fields, Reflections reflections) {
        reflections.getTypesAnnotatedWith(KPermissionRelatedModel.class).forEach(kpermissionRelationModel ->
            Arrays.stream(kpermissionRelationModel.getDeclaredFields())
                    .filter(entityField -> entityField.isAnnotationPresent(KPermissionRelationshipFrom.class))
                    .findAny().ifPresent(field -> {
                if (field.getGenericType().equals(permissionClass)) {
                    Arrays.stream(kpermissionRelationModel.getDeclaredFields())
                            .filter(entityField -> entityField.isAnnotationPresent(KPermissionRelationshipTo.class))
                            .findAny().ifPresent(childField -> {
                        Map<String, String> fieldsData = new HashMap<>();
                        fieldsData.put(FIELD_NAME, childField.getName());
                        fields.add(fieldsData);
                    });
                }
            })
        );
    }

    private List<Map<String, Object>> findSubModelData(Class permissionClass) {
        List<Map<String, Object>> subModelData = new ArrayList<>();
        Arrays.stream(permissionClass.getDeclaredFields())
                .filter(entityField -> entityField.isAnnotationPresent(KPermissionSubModel.class))
                .forEach(permissionField -> {
                    Map<String, Object> subModelMetaData = new HashMap<>();
                    Set<Map<String, String>> subModelFields = new HashSet<>();
                    if (Collection.class.isAssignableFrom(permissionField.getType())) {
                        Type genericFieldType = permissionField.getGenericType();
                        ParameterizedType aType = (ParameterizedType) genericFieldType;
                        Type[] fieldArgTypes = aType.getActualTypeArguments();
                        for (Type fieldArgType : fieldArgTypes) {
                            Class fieldArgClass = (Class) fieldArgType;
                            subModelMetaData.put(MODEL_CLASS, fieldArgClass.toString());
                            getFieldsOFModelAndSubModel(fieldArgClass.getDeclaredFields(), subModelFields);
                        }
                    } else {
                        subModelMetaData.put(MODEL_CLASS, permissionField.getType().toString());
                        getFieldsOFModelAndSubModel(permissionField.getType().getDeclaredFields(), subModelFields);
                    }
                    if (isCollectionNotEmpty(subModelFields)) {
                        subModelMetaData.put(MODEL_NAME, permissionField.getName());
                        subModelMetaData.put(FIELDS, subModelFields);
                        subModelMetaData.put(IS_PERMISSION_SUB_MODEL, true);
                        subModelData.add(subModelMetaData);
                    }
                });
        return subModelData;
    }

    private void getFieldsOFModelAndSubModel(Field[] fields, Set<Map<String, String>> subModelFields) {
        for (Field field : fields) {
            if (field.isAnnotationPresent(KPermissionField.class)) {
                Map<String, String> fieldsData = new HashMap<>();
                fieldsData.put(FIELD_NAME, field.getName());
                subModelFields.add(fieldsData);
            }
        }
    }

}

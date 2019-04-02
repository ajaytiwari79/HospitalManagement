package com.kairos.configuration;

import com.kairos.annotations.PermissionMethod;
import com.kairos.annotations.PermissionModel;
import com.kairos.annotations.PermissionSubModel;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.kairos.constants.ApplicationConstants.*;


public class PermissionSchemaScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionSchemaScanner.class);

    public List<Map<String, Object>> createPermissionSchema(String domainPackagePath){
        List<Map<String, Object>> modelData = new ArrayList<>();
        try {
            Reflections reflections = new Reflections(ClasspathHelper.forPackage(domainPackagePath));
            reflections.getTypesAnnotatedWith(PermissionModel.class).stream()
                    .forEach( permissionClass -> {
                        Map<String, Object> modelMetaData= new HashMap<>();
                        Set<Map<String, String>> fields = new HashSet<>();
                        Arrays.stream(permissionClass.getDeclaredMethods())
                                .filter(entityMethod -> entityMethod.isAnnotationPresent(PermissionMethod.class))
                                .forEach(permissionEntityMethod -> {
                                    Map<String, String> fieldsData = new HashMap<>();
                                    PermissionMethod annotation = permissionEntityMethod.getAnnotation(PermissionMethod.class);
                                    fieldsData.put(FIELD_NAME,annotation.value());
                                    fields.add(fieldsData);
                                });
                        findSubModelData(permissionClass, fields);
                        if(!fields.isEmpty()) {
                            modelMetaData.put(MODEL_NAME, permissionClass.getSimpleName());
                            modelMetaData.put(FIELDS, fields);
                            modelData.add(modelMetaData);
                        }
                    });
            LOGGER.info("model=="+modelData);

        }catch (Exception ex){
            LOGGER.error("ERROR in identifying permission models======"+ex.getMessage());
        }
        return modelData;
    }

    private void findSubModelData(Class permissionClass, Set<Map<String, String>> fields){
        Arrays.stream(permissionClass.getDeclaredFields())
                .filter(entityField -> entityField.isAnnotationPresent(PermissionSubModel.class))
                .forEach(permissionField -> {
                    if (Collection.class.isAssignableFrom(permissionField.getType())) {
                        Type genericFieldType = permissionField.getGenericType();
                        ParameterizedType aType = (ParameterizedType) genericFieldType;
                        Type[] fieldArgTypes = aType.getActualTypeArguments();
                        for (Type fieldArgType : fieldArgTypes) {
                            Class fieldArgClass = (Class) fieldArgType;
                            for (Method subModelMethod : fieldArgClass.getDeclaredMethods()) {
                                if (subModelMethod.isAnnotationPresent(PermissionMethod.class)) {
                                    Map<String, String> fieldsData = new HashMap<>();
                                    PermissionMethod annotation = subModelMethod.getAnnotation(PermissionMethod.class);
                                    fieldsData.put(FIELD_NAME,annotation.value());
                                    fields.add(fieldsData);
                                }
                            }
                        }
                    } else {
                        for (Method subModelMethod : permissionField.getType().getDeclaredMethods()) {
                            if (subModelMethod.isAnnotationPresent(PermissionMethod.class)) {
                                Map<String, String> fieldsData = new HashMap<>();
                                PermissionMethod annotation = subModelMethod.getAnnotation(PermissionMethod.class);
                                fieldsData.put(FIELD_NAME,annotation.value());
                                fields.add(fieldsData);
                            }
                        }
                    }
                });
    }

}

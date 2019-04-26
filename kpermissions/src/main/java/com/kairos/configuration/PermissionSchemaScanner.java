package com.kairos.configuration;

import com.kairos.annotations.PermissionField;
import com.kairos.annotations.PermissionModel;
import com.kairos.annotations.PermissionSubModel;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
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
                        Arrays.stream(permissionClass.getDeclaredFields())
                                .filter(entityField -> entityField.isAnnotationPresent(PermissionField.class))
                                .forEach(permissionEntityField -> {
                                    Map<String, String> fieldsData = new HashMap<>();
                                    PermissionField annotation = permissionEntityField.getAnnotation(PermissionField.class);
                                    fieldsData.put(FIELD_NAME,permissionEntityField.getName());
                                    fields.add(fieldsData);
                                });
                        List<Map<String, Object>> subModelData= findSubModelData(permissionClass, fields);
                            modelMetaData.put(MODEL_NAME, permissionClass.getSimpleName());
                            modelMetaData.put(FIELDS, fields);
                            modelMetaData.put(SUB_MODELS, subModelData);
                            modelData.add(modelMetaData);
                    });
            LOGGER.info("model=="+modelData);

        }catch (Exception ex){
            LOGGER.error("ERROR in identifying permission models======"+ex.getMessage());
        }
        return modelData;
    }

    private List<Map<String, Object>> findSubModelData(Class permissionClass, Set<Map<String, String>> fields){
        List<Map<String, Object>> subModelData = new ArrayList<>();
        Arrays.stream(permissionClass.getDeclaredFields())
                .filter(entityField -> entityField.isAnnotationPresent(PermissionSubModel.class))
                .forEach(permissionField -> {
                    Map<String, Object> subModelMetaData= new HashMap<>();
                    Set<Map<String, String>> subModelFields = new HashSet<>();
                    if (Collection.class.isAssignableFrom(permissionField.getType())) {
                        Type genericFieldType = permissionField.getGenericType();
                        ParameterizedType aType = (ParameterizedType) genericFieldType;
                        Type[] fieldArgTypes = aType.getActualTypeArguments();
                        for (Type fieldArgType : fieldArgTypes) {
                            Class fieldArgClass = (Class) fieldArgType;
                            getFieldsOFModelAndSubModel(fieldArgClass.getDeclaredFields(),subModelFields);
                            for (Field subModelField : fieldArgClass.getDeclaredFields()) {
                                if (subModelField.isAnnotationPresent(PermissionField.class)) {
                                    Map<String, String> fieldsData = new HashMap<>();
                                    fieldsData.put(FIELD_NAME,subModelField.getName());
                                    subModelFields.add(fieldsData);
                                }
                            }
                        }
                    } else {
                        getFieldsOFModelAndSubModel(permissionField.getType().getDeclaredFields(),subModelFields);
                    }
                    if(!subModelFields.isEmpty()){
                        subModelMetaData.put(MODEL_NAME, permissionField.getName());
                        subModelMetaData.put(FIELDS, subModelFields);
                        subModelData.add(subModelMetaData);
                    }
                });
        return subModelData;
    }

            private void getFieldsOFModelAndSubModel(Field[] fields, Set<Map<String, String>> subModelFields){
                for (Field field : fields) {
                    if (field.isAnnotationPresent(PermissionField.class)) {
                        Map<String, String> fieldsData = new HashMap<>();
                        fieldsData.put(FIELD_NAME,field.getName());
                        subModelFields.add(fieldsData);
                    }
                }
            }

}

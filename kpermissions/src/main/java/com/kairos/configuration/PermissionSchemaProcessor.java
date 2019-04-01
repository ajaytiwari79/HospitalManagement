package com.kairos.configuration;

import com.kairos.annotations.PermissionMethod;
import com.kairos.annotations.PermissionModel;
import com.kairos.annotations.PermissionSubModel;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.enums.IntegrationOperation;
import com.kairos.rest_client.UserRestClient;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.kairos.constants.ApplicationConstants.*;

@Component
public class PermissionSchemaProcessor  {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionSchemaProcessor.class);


    private UserRestClient userRestClient;

    private EnvConfigCommon envConfigCommon;

    public PermissionSchemaProcessor(String domainPackagePath,UserRestClient userRestClient, String userServiceUrl, EnvConfigCommon envConfigCommon) {
        this.userRestClient =userRestClient;
        this.envConfigCommon= envConfigCommon;
        createPermissionSchema(domainPackagePath, userServiceUrl);
    }

    public PermissionSchemaProcessor() {
    }

    private void createPermissionSchema(String domainPackagePath, String userServiceUrl){
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
            if("true".equalsIgnoreCase(envConfigCommon.getKpermissionDataPublish()) ) {
                Boolean result = userRestClient.publishRequest(modelData, userServiceUrl, IntegrationOperation.CREATE, "create_permission_schema", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                });
            }

        }catch (Exception ex){
            LOGGER.error("ERROR in identifying permission models======"+ex.getMessage());
        }
    }

    public void findSubModelData(Class permissionClass, Set<Map<String, String>> fields){
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

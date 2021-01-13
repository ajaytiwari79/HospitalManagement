package com.kairos.service.kpermissions;

import com.kairos.annotations.KPermissionRelatedModel;
import com.kairos.commons.annotation.PermissionClass;
import com.kairos.dto.kpermissions.FieldPermissionUserData;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.repository.activity.CommonRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.PermissionMapperUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.kpermissions.FieldLevelPermission.READ;


@Service
public class ActivityPermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityPermissionService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject private CommonRepository commonRepository;
    @Inject private UserIntegrationService userIntegrationService;


    public <T,E extends MongoBaseEntity> void updateModelBasisOfPermission(List<T> objects, Set<FieldLevelPermission> fieldLevelPermissions){
        try {
            if(UserContext.getUserDetails().isSystemAdmin()){
                return;
            }
            Set<String> modelNames=getModelNames(objects);
            FieldPermissionUserData fieldPermissionUserData=userIntegrationService.getPermissionData(modelNames);
            FieldPermissionHelperDTO fieldPermissionHelperDTO=new FieldPermissionHelperDTO(objects,fieldLevelPermissions,fieldPermissionUserData);
            if(fieldLevelPermissions.contains(READ)){
                updateObjectsPropertiesBeforeSend(fieldPermissionHelperDTO,fieldLevelPermissions);
            }else {
                updateObjectsPropertiesBeforeSave(fieldPermissionHelperDTO,fieldLevelPermissions);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }


    public <T extends MongoBaseEntity,E extends MongoBaseEntity> void updateObjectsPropertiesBeforeSave(FieldPermissionHelperDTO fieldPermissionHelper,Set<FieldLevelPermission> fieldLevelPermissions){
        for (T object : (List<T>)fieldPermissionHelper.getObjects()) {
            if(object.getClass().isAnnotationPresent(com.kairos.annotations.KPermissionModel.class) || object.getClass().isAnnotationPresent(PermissionClass.class)){
                E databaseObject = (E)fieldPermissionHelper.getMapOfDataBaseObject().get(object.getId());
                PermissionMapperUtils.PermissionHelper permissionHelper = fieldPermissionHelper.getPermissionHelper(object.getClass().getSimpleName(),fieldLevelPermissions);
                if(PermissionMapperUtils.personalisedModel.contains(object.getClass().getSimpleName())){
                    permissionHelper.setOtherPermissions(permissionHelper.getOtherPermissionDTOMap().getOrDefault(object.getId(),new OtherPermissionDTO()));
                }
                PermissionMapperUtils.copySpecificPropertiesByMapper(object,databaseObject,permissionHelper);
            }/*else if(object.getClass().isAnnotationPresent(KPermissionRelatedModel.class)){

            }*/
        }
    }


    @Getter
    @Setter
    public class FieldPermissionHelperDTO<T extends MongoBaseEntity, E extends MongoBaseEntity> {
        private List<T> objects;
        private Map<String, ModelDTO> modelMap;
        private Map<Long, E> mapOfDataBaseObject;
        private Map<Long, OtherPermissionDTO> otherPermissionDTOMap;
        private Long currentUserStaffId;
        private boolean systemAdmin;
        private Long staffId;
        private FieldPermissionUserData fieldPermissionUserData;

        private FieldPermissionHelperDTO(List<T> objects,Set<FieldLevelPermission> fieldLevelPermissions,FieldPermissionUserData fieldPermissionUserData) {
            this.objects = objects;
            systemAdmin = UserContext.getUserDetails().isSystemAdmin();
            List<ModelDTO> modelDTOS = fieldPermissionUserData.getModelDTOS();
            modelMap = modelDTOS.stream().collect(Collectors.toMap(k -> k.getModelName(), v -> v));
            Map[] mapArray = getObjectByIds(objects,fieldLevelPermissions);
            mapOfDataBaseObject = mapArray[0];
            otherPermissionDTOMap = mapArray[1];
            currentUserStaffId = fieldPermissionUserData.getCurrentUserStaffId();
        }



        private <ID,E,T> Map[] getObjectByIds(List<T> objects,Set<FieldLevelPermission> fieldLevelPermissions){
            Map<Class,Set<ID>> objectIdsMap = new HashMap<>();
            for (T object : objects) {
                    try {
                        ID id = (ID) object.getClass().getMethod("getId").invoke(object);
                        if (isNotNull(id)) {
                            Set<ID> ids = objectIdsMap.getOrDefault(object.getClass(), new HashSet<>());
                            ids.add(id);
                            objectIdsMap.put(object.getClass(), ids);
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        LOGGER.error(e.getMessage());
                    }
            }
            Map<ID,E> mapOfDataBaseObject = new HashMap<>();
            if(fieldLevelPermissions.contains(FieldLevelPermission.WRITE)){
                for (Map.Entry<Class, Set<ID>> classIdSetEntry : objectIdsMap.entrySet()) {
                    Collection<E> databaseObject = commonRepository.findAllByIds(classIdSetEntry.getKey(),classIdSetEntry.getValue());
                    for (E object : databaseObject) {
                        try {
                            mapOfDataBaseObject.put((ID) object.getClass().getMethod("getId").invoke(object),object);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                }
            }
            Map<Long, OtherPermissionDTO> staffPermissionRelatedDataQueryResultMap = new HashMap<>();
            return new Map[]{mapOfDataBaseObject,staffPermissionRelatedDataQueryResultMap};
        }

        public PermissionMapperUtils.PermissionHelper getPermissionHelper(String className,Set<FieldLevelPermission> fieldLevelPermissions){
            return new PermissionMapperUtils.PermissionHelper(modelMap.get(className),currentUserStaffId,otherPermissionDTOMap, systemAdmin,fieldLevelPermissions);
        }
    }

    private <T> Set<String> getModelNames(List<T> objects) {
        return objects.stream().map(model->{
            if(model.getClass().isAnnotationPresent(com.kairos.annotations.KPermissionModel.class)) {
                return model.getClass().getSimpleName();
            }else if(model.getClass().isAnnotationPresent(PermissionClass.class)){
                PermissionClass permissionClass = model.getClass().getAnnotation(PermissionClass.class);
                return permissionClass.name();
            }else if(model.getClass().isAnnotationPresent(KPermissionRelatedModel.class)){
                //return getRelationShipModelPermissionModelName(model.getClass());
            }
            return "";
        }).collect(Collectors.toSet());
    }

    public <T ,E> void updateObjectsPropertiesBeforeSend(FieldPermissionHelperDTO fieldPermissionHelper,Set<FieldLevelPermission> fieldLevelPermissions) throws NoSuchFieldException, IllegalAccessException {
        for (T object : (List<T>)fieldPermissionHelper.getObjects()) {
            if(object.getClass().isAnnotationPresent(com.kairos.annotations.KPermissionModel.class) || object.getClass().isAnnotationPresent(PermissionClass.class)){
                Field field=object.getClass().getDeclaredField("id");
                field.setAccessible(true);
                BigInteger objectId=new BigInteger(field.get(object).toString());
                String className=!object.getClass().isAnnotationPresent(PermissionClass.class)?object.getClass().getSimpleName():object.getClass().getAnnotation(PermissionClass.class).name();
                E databaseObject = (E)fieldPermissionHelper.getMapOfDataBaseObject().get(objectId);
                PermissionMapperUtils.PermissionHelper permissionHelper = fieldPermissionHelper.getPermissionHelper(className,fieldLevelPermissions);
                if(PermissionMapperUtils.personalisedModel.contains(className)){
                    permissionHelper.setOtherPermissions(permissionHelper.getOtherPermissionDTOMap().getOrDefault(objectId,new OtherPermissionDTO()));
                }
                PermissionMapperUtils.copySpecificPropertiesByMapper(object,databaseObject,permissionHelper);
            }/*else if(object.getClass().isAnnotationPresent(KPermissionRelatedModel.class)){

            }*/
        }
    }

}

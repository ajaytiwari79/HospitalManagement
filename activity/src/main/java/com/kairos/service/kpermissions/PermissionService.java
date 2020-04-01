package com.kairos.service.kpermissions;

import com.kairos.annotations.KPermissionRelatedModel;
import com.kairos.commons.annotation.PermissionClass;
import com.kairos.dto.kpermissions.FieldPermissionUserData;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.PermissionMapperUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;


@Service
public class PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject private MongoBaseRepository commonRepository;
    @Inject private UserIntegrationService userIntegrationService;


    public <T,E extends MongoBaseEntity> void updateModelBasisOfPermission(List<T> objects, Set<FieldLevelPermission> fieldLevelPermissions){
        try {
            if(UserContext.getUserDetails().isHubMember()){
                return;
            }
            FieldPermissionUserData fieldPermissionUserData=userIntegrationService.getModels(objects);
            FieldPermissionHelperDTO fieldPermissionHelperDTO=new FieldPermissionHelperDTO(objects,fieldLevelPermissions,fieldPermissionUserData);
            updateObjectsPropertiesBeforeSave(fieldPermissionHelperDTO,fieldLevelPermissions);
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
                    permissionHelper.setSameStaff(permissionHelper.getCurrentUserStaffId().equals(object.getId()));
                    permissionHelper.setOtherPermissions(permissionHelper.getOtherPermissionDTOMap().getOrDefault(object.getId(),new OtherPermissionDTO()));
                }
                PermissionMapperUtils.copySpecificPropertiesByMapper(object,databaseObject,permissionHelper);
            }/*else if(object.getClass().isAnnotationPresent(KPermissionRelatedModel.class)){

            }*/
        }
    }


    @Getter
    @Setter
    private class FieldPermissionHelperDTO<T extends MongoBaseEntity, E extends MongoBaseEntity> {
        private List<T> objects;
        private Map<String, ModelDTO> modelMap;
        private Map<Long, E> mapOfDataBaseObject;
        private Map<Long, OtherPermissionDTO> otherPermissionDTOMap;
        private Long currentUserStaffId;
        private boolean hubMember;
        private Long staffId;
        private FieldPermissionUserData fieldPermissionUserData;

        public FieldPermissionHelperDTO(List<T> objects,Set<FieldLevelPermission> fieldLevelPermissions,FieldPermissionUserData fieldPermissionUserData) {
            this.objects = objects;
            hubMember = UserContext.getUserDetails().isHubMember();
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
                if(!object.getClass().isAnnotationPresent(KPermissionRelatedModel.class)){
                    try {
                        ID id = (ID) object.getClass().getMethod("getId").invoke(object);
                        if (isNotNull(id)) {
                            Set<ID> ids = objectIdsMap.getOrDefault(object.getClass(), new HashSet<>());
                            ids.add(id);
                            objectIdsMap.put(object.getClass(), ids);
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            Map<ID,E> mapOfDataBaseObject = new HashMap<>();
            if(fieldLevelPermissions.contains(FieldLevelPermission.WRITE)){
                for (Map.Entry<Class, Set<ID>> classIdSetEntry : objectIdsMap.entrySet()) {
                    Collection<E> databaseObject = commonRepository.findAllById(classIdSetEntry.getValue());
                    for (E object : databaseObject) {
                        try {
                            ID id = (ID) object.getClass().getMethod("getId").invoke(object);
                            mapOfDataBaseObject.put(id,object);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Map<Long, OtherPermissionDTO> staffPermissionRelatedDataQueryResultMap = new HashMap<>();
//            if(objectIdsMap.containsKey(Staff.class)){
//                 staffPermissionRelatedDataQueryResultMap = staffService.getStaffDataForPermissionByStaffIds((Set<Long>)objectIdsMap.get(Staff.class));
//            }
            return new Map[]{mapOfDataBaseObject,staffPermissionRelatedDataQueryResultMap};
        }

        public PermissionMapperUtils.PermissionHelper getPermissionHelper(String className,Set<FieldLevelPermission> fieldLevelPermissions){
            return new PermissionMapperUtils.PermissionHelper(modelMap.get(className),currentUserStaffId,otherPermissionDTOMap,hubMember,fieldLevelPermissions);
        }
    }

}

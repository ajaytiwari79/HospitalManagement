package com.kairos.service.kpermissions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.kpermissions.*;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.kpermissions.*;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.kpermissions.*;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.utils.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.constants.ApplicationConstants.*;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_DATANOTFOUND;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_PERMISSION_FIELD;

@Service
public class PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    @Inject
    private PermissionModelRepository permissionModelRepository;

    @Inject
    private AccessGroupRepository accessGroupRepository;

    @Inject
    private PermissionFieldRepository permissionFieldRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AccessGroupPermissionFieldRelationshipGraphRepository accessGroupPermissionFieldRelationshipGraphRepository;

    @Inject
    private AccessGroupPermissionModelRelationshipGraphRepository accessGroupPermissionModelRelationshipGraphRepository;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private StaffGraphRepository staffGraphRepository;

    public List<ModelDTO> createPermissionSchema(List<ModelDTO> modelDTOS){
        List<KPermissionModel> kPermissionModels = new ArrayList<>();
        permissionModelRepository.findAll().iterator().forEachRemaining(kPermissionModels::add);
        kPermissionModels = kPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        buildPermissionModelData(modelDTOS, kPermissionModels, false);
        permissionModelRepository.save(kPermissionModels,2);
        return modelDTOS;
    }

    private List<KPermissionModel> buildPermissionModelData(List<ModelDTO> modelDTOS, List<KPermissionModel> kPermissionModelList, boolean isSubModel){
        List<ModelDTO> newModelDTO = new ArrayList<>();
        List<KPermissionModel> kPermissionModels = new ArrayList<>();
        modelDTOS.forEach(modelDTO -> {
            Optional<KPermissionModel>  permissionModelObj = kPermissionModelList.stream().filter(permissionModel ->
                    modelDTO.getModelName().equalsIgnoreCase(permissionModel.getModelName())
            ).findAny();
            if(permissionModelObj.isPresent()){
                KPermissionModel kPermissionModel = permissionModelObj.get();
                List<String> fields = kPermissionModel.getFields().stream().map(KPermissionField::getFieldName).collect(Collectors.toList());
                modelDTO.getFields().forEach(fieldDTO -> {
                    if(!fields.contains(fieldDTO.getFieldName())){
                        kPermissionModel.getFields().add(new KPermissionField(fieldDTO.getFieldName()));
                    }
                });
                kPermissionModel.setPermissionSubModel(isSubModel);
                if(!modelDTO.getSubModels().isEmpty()){
                    kPermissionModel.getSubModels().addAll(buildPermissionModelData(modelDTO.getSubModels(), kPermissionModel.getSubModels(), true));
                }
                    kPermissionModels.add(kPermissionModel);
            }else{
                newModelDTO.add(modelDTO);
            }

        });
        kPermissionModelList.addAll(ObjectMapperUtils.copyPropertiesOfListByMapper(newModelDTO, KPermissionModel.class));
        return kPermissionModelList;
    }

    public List<ModelDTO> getPermissionSchema(){
        Map<String, Object> permissionSchemaMap = new HashMap<>();
        List<KPermissionModel> kPermissionModels = new ArrayList();
        permissionModelRepository.findAll().iterator().forEachRemaining(kPermissionModels::add);
        kPermissionModels = kPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        return ObjectMapperUtils.copyPropertiesOfListByMapper(kPermissionModels, ModelDTO.class);
    }

    public Map<String, Object> getPermissionSchema(Long accessGroupId){
        Map<String, Object> permissionSchemaMap = new HashMap<>();
        List<KPermissionModel> kPermissionModels = new ArrayList();
        permissionModelRepository.findAll().iterator().forEachRemaining(kPermissionModels::add);
        kPermissionModels = kPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        List<ModelPermissionQueryResult> modelPermissionQueryResults = permissionModelRepository.getModelPermissionsByAccessGroupId(accessGroupId);
        permissionSchemaMap.put(PERMISSIONS_SCHEMA,ObjectMapperUtils.copyPropertiesOfListByMapper(kPermissionModels, ModelDTO.class));
        permissionSchemaMap.put(PERMISSIONS, Stream.of(FieldLevelPermission.values()).map(FieldLevelPermission::getValue).collect(Collectors.toList()));
        permissionSchemaMap.put(PERMISSION_DATA, modelPermissionQueryResults);
            return permissionSchemaMap;
    }

    public List<PermissionDTO> createPermissions(List<PermissionDTO> permissionDTOList){
        permissionDTOList.forEach(permissionDTO -> {
            //List<AccessGroup> accessGroups = accessGroupRepository.findAllById(permissionDTO.getAccessGroupIds());
            linkAccessGroupToSubModelAndPermissionFields(permissionDTO.getModelPermissions(), permissionDTO.getAccessGroupIds());
        });
        return permissionDTOList;
    }


    public void linkAccessGroupToSubModelAndPermissionFields(List<ModelPermissionDTO> modelPermissionDTOS, List<Long> accessGroupIds){
        modelPermissionDTOS.forEach(modelPermissionDTO -> {
            KPermissionModel kPermissionModel = null;
            for(FieldPermissionDTO fieldPermissionDTO : modelPermissionDTO.getFieldPermissions()){
                KPermissionFieldQueryResult KPermissionFieldQueryResult = permissionFieldRepository.getPermissionFieldByIdAndPermissionModelId(modelPermissionDTO.getPermissionModelId(), fieldPermissionDTO.getFieldId());
                if(KPermissionFieldQueryResult == null){
                    exceptionService.dataNotFoundByIdException("message.permission.KPermissionFieldQueryResult");
                }
                kPermissionModel = KPermissionFieldQueryResult.getKPermissionModel();
                KPermissionField KPermissionField = KPermissionFieldQueryResult.getKPermissionField();
                if(KPermissionField == null){
                    exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_PERMISSION_FIELD, fieldPermissionDTO.getFieldId());
                }else{
                        accessGroupPermissionFieldRelationshipGraphRepository.createAccessGroupPermissionFieldRelationshipType(KPermissionField.getId(),accessGroupIds,fieldPermissionDTO.getFieldPermission());
                }
            }
                accessGroupPermissionModelRelationshipGraphRepository.createAccessGroupPermissionModelRelationship(kPermissionModel.getId(), accessGroupIds,modelPermissionDTO.getModelPermission());
            if(!modelPermissionDTO.getSubModelPermissions().isEmpty()){
                linkAccessGroupToSubModelAndPermissionFields(modelPermissionDTO.getSubModelPermissions(), accessGroupIds);
            }
        });
    }

    public <E extends Object, T extends UserBaseEntity> E evaluatePermission(@Valid E dtoObject, Class<T> modelClass, List<FieldLevelPermission> permissions){
        try {

            Organization organization = organizationService.fetchParentOrganization(UserContext.getUnitId());
            Long staffId = staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), organization.getId());
            StaffAccessGroupQueryResult staffAccessGroupQueryResult =  accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId, organization.getId());
            List<Long> accessGroupIds = staffAccessGroupQueryResult.getAccessGroupIds();
            //List<String> permissionFields = permissionFieldRepository.findPermissionFieldsByAccessGroupAndModelClass(modelClass.toString(), accessGroupIds,permissions);
            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
            filterProvider.addFilter("permissionValidatorFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("firstName"));
            ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();
            objectMapper.setFilterProvider(filterProvider);
            String jsonData = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(dtoObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T extends UserBaseEntity> List<KPermissionModelFieldDTO> fetchPermissionFields(Class<T> modelClass, List<FieldLevelPermission> permissions){
        List<KPermissionModelFieldDTO> kPermissionModelFieldDTO = new ArrayList<>() ;
        try {
            Organization organization = organizationService.fetchParentOrganization(UserContext.getUnitId());
            Long staffId = staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), organization.getId());
            StaffAccessGroupQueryResult staffAccessGroupQueryResult =  accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId, organization.getId());
            List<Long> accessGroupIds = staffAccessGroupQueryResult.getAccessGroupIds();
            List<List<String>> permissionFields = permissionFieldRepository.findPermissionFieldsByAccessGroupAndModelClass(modelClass.toString(), accessGroupIds,permissions);
            List<KPermissionSubModelFieldQueryResult> kPermissionSubModelFieldQueryResults = permissionFieldRepository.findSubModelPermissionFieldsByAccessGroupAndModelClass(modelClass.toString(), accessGroupIds,permissions);
            kPermissionSubModelFieldQueryResults.add(new KPermissionSubModelFieldQueryResult(modelClass.getSimpleName(),permissionFields.get(0)));
            kPermissionModelFieldDTO = ObjectMapperUtils.copyPropertiesOfListByMapper(kPermissionSubModelFieldQueryResults,KPermissionModelFieldDTO.class );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kPermissionModelFieldDTO;
    }
}

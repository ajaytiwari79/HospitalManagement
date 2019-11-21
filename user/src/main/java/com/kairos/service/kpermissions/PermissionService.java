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
import com.kairos.persistence.repository.kpermissions.PermissionFieldRepository;
import com.kairos.persistence.repository.kpermissions.PermissionModelRepository;
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
import java.util.stream.StreamSupport;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
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
    private OrganizationService organizationService;

    @Inject
    private StaffGraphRepository staffGraphRepository;

    public List<ModelDTO> createPermissionSchema(List<ModelDTO> modelDTOS){
        List<KPermissionModel> kPermissionModels = StreamSupport.stream(permissionModelRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
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
        List<KPermissionModel> kPermissionModels = new ArrayList();
        permissionModelRepository.findAll().iterator().forEachRemaining(kPermissionModels::add);
        kPermissionModels = kPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        return ObjectMapperUtils.copyPropertiesOfListByMapper(kPermissionModels, ModelDTO.class);
    }

    public Map<String, Object> getPermissionSchema(List<Long> accessGroupIds){
        Map<String, Object> permissionSchemaMap = new HashMap<>();
        List<KPermissionModel> kPermissionModels = getkPermissionModels();
        //List<ModelPermissionQueryResult> modelPermissionQueryResults = permissionModelRepository.getModelPermissionsByAccessGroupId(accessGroupId);

        permissionSchemaMap.put(PERMISSIONS_SCHEMA,ObjectMapperUtils.copyPropertiesOfListByMapper(kPermissionModels, ModelDTO.class));
        permissionSchemaMap.put(PERMISSIONS, FieldLevelPermission.values());
        permissionSchemaMap.put(PERMISSION_DATA, getModelPermission(kPermissionModels,accessGroupIds));
            return permissionSchemaMap;
    }

    private List<KPermissionModel> getkPermissionModels() {
        List<KPermissionModel> kPermissionModels = new ArrayList();
        permissionModelRepository.findAll().forEach(kPermissionModel -> {
            if(!kPermissionModel.isPermissionSubModel()){
                kPermissionModels.add(kPermissionModel);
            }
        });
        return kPermissionModels;
    }

    private Map[] getMapOfPermission(Collection<Long> accessGroupIds) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = permissionModelRepository.getAllModelPermission(accessGroupIds);
        List<FieldPermissionQueryResult> fieldLevelPermissions = permissionModelRepository.getAllFieldPermission(accessGroupIds);
        Map<Long,Set<FieldLevelPermission>> fieldLevelPermissionMap = new HashMap<>();
        Map<Long,Set<FieldLevelPermission>> modelPermissionMap = new HashMap<>();
        if(isCollectionNotEmpty(modelPermissionQueryResults)){
            modelPermissionMap = modelPermissionQueryResults.stream().collect(Collectors.toMap(modelPermissionQueryResult -> modelPermissionQueryResult.getPermissionModelId(),v->getFieldPermissionByPriority(v.getModelPermissions())));
        }
        if(isCollectionNotEmpty(fieldLevelPermissions)){
            fieldLevelPermissionMap = fieldLevelPermissions.stream().collect(Collectors.toMap(modelPermissionQueryResult -> modelPermissionQueryResult.getFieldId(),v->getFieldPermissionByPriority(v.getFieldPermissions())));
        }
        return new Map[]{modelPermissionMap,fieldLevelPermissionMap};
    }

    private Set<FieldLevelPermission> getFieldPermissionByPriority(Set<FieldLevelPermission> fieldLevelPermissions){
        if(fieldLevelPermissions.size()>1){
            if(fieldLevelPermissions.contains(FieldLevelPermission.WRITE)){
                fieldLevelPermissions.removeIf(fieldLevelPermission->!FieldLevelPermission.WRITE.equals(fieldLevelPermission));
            }else if(fieldLevelPermissions.contains(FieldLevelPermission.READ)){
                fieldLevelPermissions.remove(FieldLevelPermission.HIDE);
            }
        }
        return fieldLevelPermissions;
    }

    private List<ModelPermissionQueryResult> getModelPermission(List<KPermissionModel> kPermissionModels,Collection<Long> accessGroupIds){
        Map[] permissionMap = getMapOfPermission(accessGroupIds);
        Map<Long,Set<FieldLevelPermission>> modelPermissionMap = permissionMap[0];
        Map<Long,Set<FieldLevelPermission>> fieldLevelPermissionMap = permissionMap[1];
        List<ModelPermissionQueryResult> modelPermissionQueryResults = getModelPermissionQueryResults(kPermissionModels, modelPermissionMap, fieldLevelPermissionMap);
        return modelPermissionQueryResults;
    }

    private List<ModelPermissionQueryResult> getModelPermissionQueryResults(List<KPermissionModel> kPermissionModels, Map<Long, Set<FieldLevelPermission>> modelPermissionMap, Map<Long, Set<FieldLevelPermission>> fieldLevelPermissionMap) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = new ArrayList<>();
        for (KPermissionModel kPermissionModel : kPermissionModels) {
            modelPermissionQueryResults.add(new ModelPermissionQueryResult(kPermissionModel.getId(),getFieldLevelPermissionQueryResult(fieldLevelPermissionMap,kPermissionModel.getFields()),getModelPermissionQueryResults(kPermissionModel.getSubModels(),modelPermissionMap,fieldLevelPermissionMap),modelPermissionMap.get(kPermissionModel.getId())));
        }
        return modelPermissionQueryResults;
    }

    private List<FieldPermissionQueryResult> getFieldLevelPermissionQueryResult(Map<Long,Set<FieldLevelPermission>> fieldLevelPermissionMap,List<KPermissionField> fields){
        List<FieldPermissionQueryResult> fieldPermissionQueryResults = new ArrayList<>();
        for (KPermissionField field : fields) {
            fieldPermissionQueryResults.add(new FieldPermissionQueryResult(field.getId(),fieldLevelPermissionMap.getOrDefault(field.getId(),new HashSet<>())));
        }
        return fieldPermissionQueryResults;
    }

    public PermissionDTO createPermissions(PermissionDTO permissionDTO){

        linkAccessGroupToSubModelAndPermissionFields(permissionDTO.getModelPermissions(), permissionDTO.getAccessGroupIds());
        return permissionDTO;
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
                        permissionModelRepository.createAccessGroupPermissionFieldRelationshipType(KPermissionField.getId(),accessGroupIds,fieldPermissionDTO.getFieldPermissions());
                }
            }
            permissionModelRepository.createAccessGroupPermissionModelRelationship(kPermissionModel.getId(), accessGroupIds,modelPermissionDTO.getModelPermissions());
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
            LOGGER.error(e.getMessage());
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
            LOGGER.error(e.getMessage());
        }
        return kPermissionModelFieldDTO;
    }
}

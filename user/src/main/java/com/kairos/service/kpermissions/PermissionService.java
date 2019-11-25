package com.kairos.service.kpermissions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.kpermissions.*;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.kpermissions.*;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import com.kairos.persistence.repository.kpermissions.CommonRepository;
import com.kairos.persistence.repository.kpermissions.PermissionFieldRepository;
import com.kairos.persistence.repository.kpermissions.PermissionModelRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
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
    @Inject private AccessGroupService accessGroupService;
    @Inject private CommonRepository commonRepository;

    public List<ModelDTO> createPermissionSchema(List<ModelDTO> modelDTOS){
        Map<String,KPermissionModel> modelNameAndModelMap = StreamSupport.stream(permissionModelRepository.findAll().spliterator(), false).filter(it -> !it.isPermissionSubModel()).collect(Collectors.toMap(k->k.getModelName().toLowerCase(),v->v));
        List<KPermissionModel> kPermissionModels = buildPermissionModelData(modelDTOS, modelNameAndModelMap, false);
        permissionModelRepository.save(kPermissionModels,2);
        return modelDTOS;
    }

    private List<KPermissionModel> buildPermissionModelData(List<ModelDTO> modelDTOS, Map<String,KPermissionModel> modelNameAndModelMap, boolean isSubModel){
        List<ModelDTO> newModelDTO = new ArrayList<>();
        List<KPermissionModel> kPermissionModels = new ArrayList<>();
        modelDTOS.forEach(modelDTO -> {
            if(modelNameAndModelMap.containsKey(modelDTO.getModelName().toLowerCase())){
                KPermissionModel kPermissionModel = updateModelSchemma(modelNameAndModelMap, modelDTO);
                kPermissionModel.setPermissionSubModel(isSubModel);
                updateSubmodelSchema(modelDTO, kPermissionModel);
                kPermissionModels.add(kPermissionModel);
            }else{
                updateModel(isSubModel, modelDTO);
                newModelDTO.add(modelDTO);

            }

        });
        kPermissionModels.addAll(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(newModelDTO, KPermissionModel.class));
        return kPermissionModels;
    }

    private void updateModel(boolean isSubModel, ModelDTO modelDTO) {
        modelDTO.setOrganizationCategories(new HashSet<>());
        modelDTO.getFields().forEach(fieldDTO -> fieldDTO.setOrganizationCategories(new HashSet<>()));
        modelDTO.setPermissionSubModel(isSubModel);
        if(isCollectionNotEmpty(modelDTO.getSubModels())){
            modelDTO.getSubModels().forEach(modelDTO1 ->updateModel(isSubModel,modelDTO1));
        }
    }

    private KPermissionModel updateModelSchemma(Map<String, KPermissionModel> modelNameAndModelMap, ModelDTO modelDTO) {
        KPermissionModel kPermissionModel = modelNameAndModelMap.get(modelDTO.getModelName().toLowerCase());
        kPermissionModel.setOrganizationCategories(new HashSet<>());
        Set<String> fields = kPermissionModel.getFields().stream().map(KPermissionField::getFieldName).collect(Collectors.toSet());
        modelDTO.getFields().forEach(fieldDTO -> {
            if(!fields.contains(fieldDTO.getFieldName())){
                kPermissionModel.getFields().add(new KPermissionField(fieldDTO.getFieldName(),new HashSet<>()));
            }
        });
        return kPermissionModel;
    }

    private void updateSubmodelSchema(ModelDTO modelDTO, KPermissionModel kPermissionModel) {
        if (!modelDTO.getSubModels().isEmpty()) {
            Map<String,KPermissionModel> subModelNameAndModelMap = new HashMap<>();
            if(isCollectionNotEmpty(kPermissionModel.getSubModels())){
                subModelNameAndModelMap = kPermissionModel.getSubModels().stream().collect(Collectors.toMap(k->k.getModelName().toLowerCase(), v->v));
            }
            kPermissionModel.getSubModels().addAll(buildPermissionModelData(modelDTO.getSubModels(), subModelNameAndModelMap, true));
        }
    }

    public List<ModelDTO> getPermissionSchema(){
        List<KPermissionModel> kPermissionModels = new ArrayList();
        permissionModelRepository.findAll().iterator().forEachRemaining(kPermissionModels::add);
        kPermissionModels = kPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(kPermissionModels, ModelDTO.class);
    }

    public Map<String, Object> getPermissionSchema(List<Long> accessGroupIds){
        Map<String, Object> permissionSchemaMap = new HashMap<>();
        List<KPermissionModel> kPermissionModels = getkPermissionModels();
        //List<ModelPermissionQueryResult> modelPermissionQueryResults = permissionModelRepository.getModelPermissionsByAccessGroupId(accessGroupId);

        permissionSchemaMap.put(PERMISSIONS_SCHEMA,ObjectMapperUtils.copyPropertiesOfCollectionByMapper(kPermissionModels, ModelDTO.class));
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
            modelPermissionMap = modelPermissionQueryResults.stream().collect(Collectors.toMap(ModelPermissionQueryResult::getPermissionModelId,v->getFieldPermissionByPriority(v.getPermissions())));
        }
        if(isCollectionNotEmpty(fieldLevelPermissions)){
            fieldLevelPermissionMap = fieldLevelPermissions.stream().collect(Collectors.toMap(FieldPermissionQueryResult::getFieldId,v->getFieldPermissionByPriority(v.getPermissions())));
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
        return getModelPermissionQueryResults(kPermissionModels, modelPermissionMap, fieldLevelPermissionMap);
    }

    private List<ModelPermissionQueryResult> getModelPermissionQueryResults(List<KPermissionModel> kPermissionModels, Map<Long, Set<FieldLevelPermission>> modelPermissionMap, Map<Long, Set<FieldLevelPermission>> fieldLevelPermissionMap) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = new ArrayList<>();
        for (KPermissionModel kPermissionModel : kPermissionModels) {
            modelPermissionQueryResults.add(new ModelPermissionQueryResult(kPermissionModel.getId(),kPermissionModel.getModelName(),getFieldLevelPermissionQueryResult(fieldLevelPermissionMap,kPermissionModel.getFields()),getModelPermissionQueryResults(kPermissionModel.getSubModels(),modelPermissionMap,fieldLevelPermissionMap),modelPermissionMap.get(kPermissionModel.getId())));
        }
        return modelPermissionQueryResults;
    }

    private List<FieldPermissionQueryResult> getFieldLevelPermissionQueryResult(Map<Long,Set<FieldLevelPermission>> fieldLevelPermissionMap,List<KPermissionField> fields){
        List<FieldPermissionQueryResult> fieldPermissionQueryResults = new ArrayList<>();
        for (KPermissionField field : fields) {
            fieldPermissionQueryResults.add(new FieldPermissionQueryResult(field.getId(),field.getFieldName(),fieldLevelPermissionMap.getOrDefault(field.getId(),new HashSet<>())));
        }
        return fieldPermissionQueryResults;
    }

    public PermissionDTO createPermissions(PermissionDTO permissionDTO,boolean updateOrganisationCategories){
        updateOrganisationCategoryOrPermissions(permissionDTO.getModelPermissions(), permissionDTO.getAccessGroupIds(),updateOrganisationCategories);
        return permissionDTO;
    }


    public void updateOrganisationCategoryOrPermissions(List<ModelPermissionDTO> modelPermissionDTOS, List<Long> accessGroupIds, boolean updateOrganisationCategories){
        modelPermissionDTOS.forEach(modelPermissionDTO -> {
            KPermissionModel kPermissionModel = null;
            for(FieldPermissionDTO fieldPermissionDTO : modelPermissionDTO.getFieldPermissions()){
                KPermissionFieldQueryResult kPermissionFieldQueryResult = getkPermissionFieldQueryResult(modelPermissionDTO, fieldPermissionDTO);
                kPermissionModel = kPermissionFieldQueryResult.getKPermissionModel();
                KPermissionField kPermissionField = kPermissionFieldQueryResult.getKPermissionField();
                updatePermissionOrOrganisationCategory(accessGroupIds, updateOrganisationCategories, fieldPermissionDTO, kPermissionField);
            }
            updateModelPermissionOrOrganisationCategory(accessGroupIds, updateOrganisationCategories, modelPermissionDTO, kPermissionModel);
            if(!modelPermissionDTO.getSubModelPermissions().isEmpty()){
                updateOrganisationCategoryOrPermissions(modelPermissionDTO.getSubModelPermissions(), accessGroupIds,updateOrganisationCategories);
            }
        });
    }

    private void updatePermissionOrOrganisationCategory(List<Long> accessGroupIds, boolean updateOrganisationCategories, FieldPermissionDTO fieldPermissionDTO, KPermissionField kPermissionField) {
        if(updateOrganisationCategories){
            kPermissionField.setOrganizationCategories(fieldPermissionDTO.getOrganizationCategories());
        }else {
            if(kPermissionField == null){
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_PERMISSION_FIELD, fieldPermissionDTO.getFieldId());
            }else{
                permissionModelRepository.createAccessGroupPermissionFieldRelationshipType(kPermissionField.getId(),accessGroupIds,fieldPermissionDTO.getFieldPermissions());
            }
        }
    }

    private void updateModelPermissionOrOrganisationCategory(List<Long> accessGroupIds, boolean updateOrganisationCategories, ModelPermissionDTO modelPermissionDTO, KPermissionModel kPermissionModel) {
        if(updateOrganisationCategories){
            kPermissionModel.setOrganizationCategories(modelPermissionDTO.getOrganizationCategories());
        }else {
            permissionModelRepository.createAccessGroupPermissionModelRelationship(kPermissionModel.getId(), accessGroupIds, modelPermissionDTO.getModelPermissions());
        }
    }

    private KPermissionFieldQueryResult getkPermissionFieldQueryResult(ModelPermissionDTO modelPermissionDTO, FieldPermissionDTO fieldPermissionDTO) {
        KPermissionFieldQueryResult kPermissionFieldQueryResult = permissionFieldRepository.getPermissionFieldByIdAndPermissionModelId(modelPermissionDTO.getPermissionModelId(), fieldPermissionDTO.getFieldId());
        if(kPermissionFieldQueryResult == null){
            exceptionService.dataNotFoundByIdException("message.permission.KPermissionFieldQueryResult");
        }
        return kPermissionFieldQueryResult;
    }

    public <E extends Object, T extends UserBaseEntity> E evaluatePermission(@Valid E dtoObject){
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
            if(isNotNull(staffAccessGroupQueryResult)){
                List<Long> accessGroupIds = staffAccessGroupQueryResult.getAccessGroupIds();
                List<List<String>> permissionFields = permissionFieldRepository.findPermissionFieldsByAccessGroupAndModelClass(modelClass.toString(), accessGroupIds,permissions);
                List<KPermissionSubModelFieldQueryResult> kPermissionSubModelFieldQueryResults = permissionFieldRepository.findSubModelPermissionFieldsByAccessGroupAndModelClass(modelClass.toString(), accessGroupIds,permissions);
                kPermissionSubModelFieldQueryResults.add(new KPermissionSubModelFieldQueryResult(modelClass.getSimpleName(),permissionFields.get(0)));
                kPermissionModelFieldDTO = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(kPermissionSubModelFieldQueryResults,KPermissionModelFieldDTO.class );
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return kPermissionModelFieldDTO;
    }

    public <T extends UserBaseEntity,E> List<T> updateModelBasisOfPermission(List<T> objects){
        try {
            Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
            Set<String> modelNames = objects.stream().map(model->model.getClass().getName()).collect(Collectors.toSet());
            List<KPermissionModel> kPermissionModels = permissionModelRepository.getAllPermissionModelByName(modelNames);
            Long staffId = staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(),unitId );
            List<AccessGroup> accessGroups =  accessGroupService.validAccessGroupByDate(unitId,getDate());
            List<ModelPermissionQueryResult> modelPermissionQueryResults = getModelPermission(kPermissionModels,accessGroups.stream().map(accessGroup -> accessGroup.getId()).collect(Collectors.toSet()));
            List<Long> objectIds = objects.stream().filter(model->isNotNull(model.getId())).map(model->model.getId()).collect(Collectors.toList());
            List<E> dataBaseObjects = commonRepository.findAllById(objectIds);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return objects;
    }
}

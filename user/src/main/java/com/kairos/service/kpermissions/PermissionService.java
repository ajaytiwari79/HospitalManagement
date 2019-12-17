package com.kairos.service.kpermissions;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.kpermissions.FieldDTO;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.PermissionDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.kpermissions.*;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.custom_repository.CommonRepositoryImpl;
import com.kairos.persistence.repository.kpermissions.PermissionFieldRepository;
import com.kairos.persistence.repository.kpermissions.PermissionModelRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesOfCollectionByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
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

    @Inject private AccessGroupService accessGroupService;

    @Inject private CommonRepositoryImpl commonRepository;
    @Inject private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject private TeamGraphRepository teamGraphRepository;
    @Inject private EmploymentTypeGraphRepository employmentTypeGraphRepository;

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
        kPermissionModels.addAll(copyPropertiesOfCollectionByMapper(newModelDTO, KPermissionModel.class));
        return kPermissionModels;
    }

    private void updateModel(boolean isSubModel, ModelDTO modelDTO) {
        modelDTO.setOrganizationCategories(new HashSet<>());
        modelDTO.getFieldPermissions().forEach(fieldDTO -> fieldDTO.setOrganizationCategories(new HashSet<>()));
        modelDTO.getActionPermissions().forEach(actionDTO -> actionDTO.setOrganizationCategories(new HashSet<>()));
        modelDTO.setPermissionSubModel(isSubModel);
        if(isCollectionNotEmpty(modelDTO.getSubModelPermissions())){
            modelDTO.getSubModelPermissions().forEach(modelDTO1 ->updateModel(true,modelDTO1));
        }
    }

    private KPermissionModel updateModelSchemma(Map<String, KPermissionModel> modelNameAndModelMap, ModelDTO modelDTO) {
        KPermissionModel kPermissionModel = modelNameAndModelMap.get(modelDTO.getModelName().toLowerCase());
        kPermissionModel.setOrganizationCategories(new HashSet<>());
        Set<String> fields = kPermissionModel.getFieldPermissions().stream().map(KPermissionField::getFieldName).collect(Collectors.toSet());
        Set<String> actions = kPermissionModel.getActionPermissions().stream().map(KPermissionAction::getActionName).collect(Collectors.toSet());
        modelDTO.getFieldPermissions().forEach(fieldDTO -> {
            if(!fields.contains(fieldDTO.getFieldName())){
                kPermissionModel.getFieldPermissions().add(new KPermissionField(fieldDTO.getFieldName(),new HashSet<>()));
            }
        });
        modelDTO.getActionPermissions().forEach(actionDTO -> {
            if(!fields.contains(actionDTO.getActionName())){
                kPermissionModel.getActionPermissions().add(new KPermissionAction(actionDTO.getActionName(),new HashSet<>()));
            }
        });
        return kPermissionModel;
    }

    private void updateSubmodelSchema(ModelDTO modelDTO, KPermissionModel kPermissionModel) {
        if (!modelDTO.getSubModelPermissions().isEmpty()) {
            Map<String,KPermissionModel> subModelNameAndModelMap = new HashMap<>();
            if(isCollectionNotEmpty(kPermissionModel.getSubModelPermissions())){
                subModelNameAndModelMap = kPermissionModel.getSubModelPermissions().stream().collect(Collectors.toMap(k->k.getModelName().toLowerCase(), v->v));
            }
            kPermissionModel.getSubModelPermissions().addAll(buildPermissionModelData(modelDTO.getSubModelPermissions(), subModelNameAndModelMap, true));
        }
    }

    public List<ModelDTO> getPermissionSchema(){
        List<KPermissionModel> kPermissionModels = new ArrayList();
        permissionModelRepository.findAll().iterator().forEachRemaining(kPermissionModels::add);
        kPermissionModels = kPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        return copyPropertiesOfCollectionByMapper(kPermissionModels, ModelDTO.class);
    }

    public Map<String, Object> getPermissionSchema(List<Long> accessGroupIds){
        Map<String, Object> permissionSchemaMap = new HashMap<>();
        List<KPermissionModel> kPermissionModels = getkPermissionModels();
        permissionSchemaMap.put(PERMISSIONS_SCHEMA, copyPropertiesOfCollectionByMapper(kPermissionModels, ModelDTO.class));
        permissionSchemaMap.put(PERMISSIONS, FieldLevelPermission.values());
        permissionSchemaMap.put(PERMISSION_DATA, copyPropertiesOfCollectionByMapper(getModelPermission(newArrayList(),accessGroupIds,false),ModelDTO.class));
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

    private Map[] getMapOfPermission(Collection<Long> accessGroupIds,boolean hubMember) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = permissionModelRepository.getAllModelPermission(accessGroupIds);
        List<FieldPermissionQueryResult> fieldLevelPermissions = permissionModelRepository.getAllFieldPermission(accessGroupIds);
        Map<Long,FieldPermissionQueryResult> fieldLevelPermissionMap = new HashMap<>();
        Map<Long,ModelPermissionQueryResult> modelPermissionMap = new HashMap<>();
        if(isCollectionNotEmpty(modelPermissionQueryResults)){
            modelPermissionMap = modelPermissionQueryResults.stream().collect(Collectors.toMap(ModelPermissionQueryResult::getId,v->getModelPermissionByPriority(v,hubMember)));
        }
        if(isCollectionNotEmpty(fieldLevelPermissions)){
            fieldLevelPermissionMap = fieldLevelPermissions.stream().collect(Collectors.toMap(FieldPermissionQueryResult::getId,v->getFieldPermissionByPriority(v,hubMember)));
        }
        return new Map[]{modelPermissionMap,fieldLevelPermissionMap};
    }

    private FieldPermissionQueryResult getFieldPermissionByPriority(FieldPermissionQueryResult fieldPermissionQueryResult,boolean hubMember){
        fieldPermissionQueryResult.setForOtherFieldLevelPermissions(hubMember ? newHashSet(FieldLevelPermission.WRITE,FieldLevelPermission.READ) : fieldPermissionQueryResult.getForOtherFieldLevelPermissions());
        fieldPermissionQueryResult.setPermissions(hubMember ? newHashSet(FieldLevelPermission.WRITE,FieldLevelPermission.READ) : fieldPermissionQueryResult.getPermissions());
        return fieldPermissionQueryResult;
    }

    private ModelPermissionQueryResult getModelPermissionByPriority(ModelPermissionQueryResult modelPermissionQueryResult,boolean hubMember){
        modelPermissionQueryResult.setForOtherFieldLevelPermissions(hubMember ? newHashSet(FieldLevelPermission.WRITE,FieldLevelPermission.READ) : modelPermissionQueryResult.getForOtherFieldLevelPermissions());
        modelPermissionQueryResult.setPermissions(hubMember ? newHashSet(FieldLevelPermission.WRITE,FieldLevelPermission.READ) : modelPermissionQueryResult.getPermissions());
        return modelPermissionQueryResult;
    }

    public List<ModelPermissionQueryResult> getModelPermission(List<String> modelNames,Collection<Long> accessGroupIds,boolean hubMember){
        Map[] permissionMap = getMapOfPermission(accessGroupIds,hubMember);
        Map<Long,ModelPermissionQueryResult> modelPermissionMap = permissionMap[0];
        Map<Long,FieldPermissionQueryResult> fieldLevelPermissionMap = permissionMap[1];
        OrganizationCategory organizationCategory = UserContext.getUserDetails().getLastSelectedOrganizationCategory();
        List<KPermissionModel> kPermissionModels;
        if(isCollectionNotEmpty(modelNames)){
            kPermissionModels = permissionModelRepository.getAllPermissionModelByName(modelNames);
        }else {
            kPermissionModels = getkPermissionModels();
        }
        return getModelPermissionQueryResults(kPermissionModels, modelPermissionMap, fieldLevelPermissionMap,organizationCategory,hubMember);
    }

    private List<ModelPermissionQueryResult> getModelPermissionQueryResults(List<KPermissionModel> kPermissionModels, Map<Long, ModelPermissionQueryResult> modelPermissionMap, Map<Long, FieldPermissionQueryResult> fieldLevelPermissionMap,OrganizationCategory organizationCategory, boolean hubMember) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = new ArrayList<>();
        for (KPermissionModel kPermissionModel : kPermissionModels) {
            ModelPermissionQueryResult modelPermissionQueryResult = modelPermissionMap.get(kPermissionModel.getId());
            if (isCollectionNotEmpty(kPermissionModel.getFieldPermissions())) {
                modelPermissionQueryResult.setFieldPermissions(getFieldLevelPermissionQueryResult(fieldLevelPermissionMap,kPermissionModel.getFieldPermissions()));
            }
            if (isCollectionNotEmpty(kPermissionModel.getSubModelPermissions())) {
                modelPermissionQueryResult.setSubModelPermissions(getModelPermissionQueryResults(kPermissionModel.getSubModelPermissions(),modelPermissionMap,fieldLevelPermissionMap,organizationCategory,hubMember));
            }
            modelPermissionQueryResults.add(modelPermissionQueryResult);
        }
        return modelPermissionQueryResults;
    }

    private boolean isValidOrganizationCategory(OrganizationCategory organizationCategory, boolean hubMember, Set<OrganizationCategory> organizationCategories) {
        return hubMember ? hubMember : organizationCategories.contains(organizationCategory);
    }

    private List<FieldPermissionQueryResult> getFieldLevelPermissionQueryResult(Map<Long,FieldPermissionQueryResult> fieldLevelPermissionMap,List<KPermissionField> fields){
        List<FieldPermissionQueryResult> fieldPermissionQueryResults = new ArrayList<>();
        for (KPermissionField field : fields) {
            FieldPermissionQueryResult fieldLevelPermissions = fieldLevelPermissionMap.get(field.getId());
            fieldPermissionQueryResults.add(fieldLevelPermissions);
        }
        return fieldPermissionQueryResults;
    }

    public PermissionDTO createPermissions(PermissionDTO permissionDTO,boolean updateOrganisationCategories){
        updateOrganisationCategoryOrPermissions(permissionDTO.getModelPermissions(), permissionDTO.getAccessGroupIds(),updateOrganisationCategories);
        return permissionDTO;
    }


    public void updateOrganisationCategoryOrPermissions(List<ModelDTO> modelPermissionDTOS, List<Long> accessGroupIds, boolean updateOrganisationCategories){
        modelPermissionDTOS.forEach(modelPermissionDTO -> {
            KPermissionModel kPermissionModel = null;
            for(FieldDTO fieldDTO : modelPermissionDTO.getFieldPermissions()){
                KPermissionFieldQueryResult kPermissionFieldQueryResult = getkPermissionFieldQueryResult(modelPermissionDTO, fieldDTO);
                kPermissionModel = kPermissionFieldQueryResult.getKPermissionModel();
                KPermissionField kPermissionField = kPermissionFieldQueryResult.getKPermissionField();
                updatePermissionOrOrganisationCategory(accessGroupIds, updateOrganisationCategories, fieldDTO, kPermissionField);
            }
            updateModelPermissionOrOrganisationCategory(accessGroupIds, updateOrganisationCategories, modelPermissionDTO, kPermissionModel);
            if(!modelPermissionDTO.getSubModelPermissions().isEmpty()){
                updateOrganisationCategoryOrPermissions(modelPermissionDTO.getSubModelPermissions(), accessGroupIds,updateOrganisationCategories);
            }
        });
    }

    private void updatePermissionOrOrganisationCategory(List<Long> accessGroupIds, boolean updateOrganisationCategories, FieldDTO fieldDTO, KPermissionField kPermissionField) {
        if(updateOrganisationCategories){
            kPermissionField.setOrganizationCategories(fieldDTO.getOrganizationCategories());
        }else {
            if(kPermissionField == null){
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_PERMISSION_FIELD, fieldDTO.getId());
            }else{
                permissionModelRepository.createAccessGroupPermissionFieldRelationshipType(kPermissionField.getId(), accessGroupIds, fieldDTO.getPermissions(),fieldDTO.getForOtherPermission().getExpertiseIds(),fieldDTO.getForOtherPermission().getUnionIds(),fieldDTO.getForOtherPermission().getTeamIds(),fieldDTO.getForOtherPermission().getEmploymentTypeIds(),fieldDTO.getForOtherPermission().getTagIds(),fieldDTO.getForOtherPermission().getStaffStatuses(),fieldDTO.getForOtherPermission().getForOtherFieldLevelPermissions());
            }
        }
    }

    private void updateModelPermissionOrOrganisationCategory(List<Long> accessGroupIds, boolean updateOrganisationCategories, ModelDTO modelDTO, KPermissionModel kPermissionModel) {
        if(updateOrganisationCategories){
            kPermissionModel.setOrganizationCategories(modelDTO.getOrganizationCategories());
        }else {

            permissionModelRepository.createAccessGroupPermissionModelRelationship(kPermissionModel.getId(), accessGroupIds, modelDTO.getPermissions(),modelDTO.getForOtherPermission().getExpertiseIds(),modelDTO.getForOtherPermission().getUnionIds(),modelDTO.getForOtherPermission().getTeamIds(),modelDTO.getForOtherPermission().getEmploymentTypeIds(),modelDTO.getForOtherPermission().getTagIds(),modelDTO.getForOtherPermission().getStaffStatuses(),modelDTO.getForOtherPermission().getForOtherFieldLevelPermissions());
        }
    }

    private KPermissionFieldQueryResult getkPermissionFieldQueryResult(ModelDTO modelDTO, FieldDTO fieldDTO) {
        KPermissionFieldQueryResult kPermissionFieldQueryResult = permissionFieldRepository.getPermissionFieldByIdAndPermissionModelId(modelDTO.getId(), fieldDTO.getId());
        if (kPermissionFieldQueryResult == null) {
            exceptionService.dataNotFoundByIdException("message.permission.KPermissionFieldQueryResult");
        }
        return kPermissionFieldQueryResult;
    }

    public <T extends UserBaseEntity,E extends UserBaseEntity> List<T> updateModelBasisOfPermission(List<T> objects){
        try {
            Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
            Set<String> modelNames = objects.stream().map(model->model.getClass().getSimpleName()).collect(Collectors.toSet());
            List<AccessGroup> accessGroups =  accessGroupService.validAccessGroupByDate(unitId,getDate());
            boolean hubMember = UserContext.getUserDetails().isHubMember();
            List<ModelPermissionQueryResult> modelPermissionQueryResults = getModelPermission(new ArrayList(modelNames),accessGroups.stream().map(accessGroup -> accessGroup.getId()).collect(Collectors.toSet()),hubMember);
            List<ModelDTO> modelDTOS = copyPropertiesOfCollectionByMapper(modelPermissionQueryResults,ModelDTO.class);
            Map<String,ModelDTO> modelMap = modelDTOS.stream().collect(Collectors.toMap(k->k.getModelName(),v->v));
            Collection<Long> objectIds = objects.stream().filter(model->isNotNull(model.getId())).map(model->model.getId()).collect(Collectors.toList());
            Map<Long,E> mapOfDataBaseObject = getObjectByIds(objects);
            updateObjectsPropertiesBeforeSave(mapOfDataBaseObject,modelMap,objects);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return objects;
    }

    private <ID,E extends UserBaseEntity,T extends UserBaseEntity> Map<ID,E> getObjectByIds(List<T> objects){
        Map<Class,Set<ID>> objectIdsMap = new HashMap<>();
        for (T object : objects) {
            if(isNotNull(object.getId())){
                Set<ID> ids = objectIdsMap.getOrDefault(object.getClass(),new HashSet<>());
                ids.add((ID) object.getId());
                objectIdsMap.put(object.getClass(),ids);
            }
        }
        Map<ID,E> mapOfDataBaseObject = new HashMap<>();
        for (Map.Entry<Class, Set<ID>> classIdSetEntry : objectIdsMap.entrySet()) {
            Collection<E> databaseObject = commonRepository.findByIds(classIdSetEntry.getKey(),(Collection<? extends Serializable>) classIdSetEntry.getValue(),2);
            for (E object : databaseObject) {
                mapOfDataBaseObject.put((ID)object.getId(),object);
            }
        }
        return mapOfDataBaseObject;
    }



   public <T extends UserBaseEntity,E extends UserBaseEntity> void updateObjectsPropertiesBeforeSave(Map<Long,E> mapOfDataBaseObject,Map<String,ModelDTO> modelMap,List<T> objects){
        for (T object : objects) {
            ObjectMapperUtils.copySpecificPropertiesByMapper(object,mapOfDataBaseObject.get(object.getId()),modelMap.get(object.getClass().getSimpleName()));
        }
    }

    public PermissionDefaultDataDTO getDefaultDataOfPermission(Long refrenceId, ConfLevel confLevel) {
        List<TeamDTO> teamDTOS = copyPropertiesOfCollectionByMapper(teamGraphRepository.findAllTeamsInOrganization(refrenceId), TeamDTO.class);
        //List<Organization> organizations = organizationService.getU
        OrganizationDTO organizationDTO = null;
        if(ConfLevel.UNIT.equals(confLevel)){
            organizationDTO = organizationService.getOrganizationWithCountryId(refrenceId);

        }
        Long countryId = ConfLevel.COUNTRY.equals(confLevel) ? refrenceId : organizationDTO.getCountryId();
        List<ExpertiseDTO> expertises = copyPropertiesOfCollectionByMapper(expertiseGraphRepository.getExpertiesOfCountry(countryId),ExpertiseDTO.class);
        List<EmploymentTypeDTO> employmentTypeDTOS = copyPropertiesOfCollectionByMapper(employmentTypeGraphRepository.getEmploymentTypeByCountry(countryId,false), EmploymentTypeDTO.class);
        return new PermissionDefaultDataDTO(expertises,null,employmentTypeDTOS,teamDTOS,organizationDTO.getTagDTOS(),newArrayList(StaffStatusEnum.values()));
    }
}

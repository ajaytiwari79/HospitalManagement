package com.kairos.service.kpermissions;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.kpermissions.FieldDTO;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.dto.kpermissions.PermissionDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.union.UnionDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.kpermissions.*;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
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
import com.kairos.service.staff.StaffService;
import lombok.Getter;
import lombok.Setter;
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
import static com.kairos.constants.CommonConstants.DEFAULT_ID;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_DATANOTFOUND;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_PERMISSION_FIELD;

@Service
public class PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    private Set<String> personalisedModel = newHashSet("Staff");

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
    @Inject private StaffService staffService;

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
        Map<Long,List<ModelPermissionQueryResult>> groupOfModelPermission = modelPermissionQueryResults.stream().collect(Collectors.groupingBy(modelPermissionQueryResult -> modelPermissionQueryResult.getId()));
        Map<Long,List<FieldPermissionQueryResult>> groupOfFieldPermission = fieldLevelPermissions.stream().collect(Collectors.groupingBy(modelPermissionQueryResult -> modelPermissionQueryResult.getId()));
        modelPermissionQueryResults = mergeModelPermissionQueryResult(groupOfModelPermission);
        fieldLevelPermissions = mergeFieldPermissionQueryResult(groupOfFieldPermission);
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

    private List<ModelPermissionQueryResult> mergeModelPermissionQueryResult(Map<Long, List<ModelPermissionQueryResult>> groupOfModelPermission) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = new ArrayList<>();
        for (Map.Entry<Long, List<ModelPermissionQueryResult>> longListEntry : groupOfModelPermission.entrySet()) {
             Set<FieldLevelPermission> permissions = new HashSet<>();
             Set<Long> expertiseIds = new HashSet<>();
             Set<Long> unionIds = new HashSet<>();
             Set<Long> teamIds = new HashSet<>();
             Set<Long> employmentTypeIds = new HashSet<>();
             Set<Long> tagIds = new HashSet<>();
             Set<StaffStatusEnum> staffStatuses = new HashSet<>();
             Set<FieldLevelPermission> forOtherFieldLevelPermissions = new HashSet<>();
            for (ModelPermissionQueryResult modelPermissionQueryResult : longListEntry.getValue()) {
                permissions.addAll(modelPermissionQueryResult.getPermissions());
                expertiseIds.addAll(modelPermissionQueryResult.getExpertiseIds());
                unionIds.addAll(modelPermissionQueryResult.getUnionIds());
                teamIds.addAll(modelPermissionQueryResult.getTeamIds());
                tagIds.addAll(modelPermissionQueryResult.getTagIds());
                employmentTypeIds.addAll(modelPermissionQueryResult.getEmploymentTypeIds());
                staffStatuses.addAll(modelPermissionQueryResult.getStaffStatuses());
                forOtherFieldLevelPermissions.addAll(modelPermissionQueryResult.getForOtherFieldLevelPermissions());
            }
            modelPermissionQueryResults.add(new ModelPermissionQueryResult(longListEntry.getKey(),permissions,expertiseIds,unionIds,teamIds,employmentTypeIds,tagIds,staffStatuses,forOtherFieldLevelPermissions));
        }
        return modelPermissionQueryResults;
    }

    private List<FieldPermissionQueryResult> mergeFieldPermissionQueryResult(Map<Long, List<FieldPermissionQueryResult>> groupOfFieldPermission) {
        List<FieldPermissionQueryResult> fieldPermissionQueryResults = new ArrayList<>();
        for (Map.Entry<Long, List<FieldPermissionQueryResult>> longListEntry : groupOfFieldPermission.entrySet()) {
            Set<FieldLevelPermission> permissions = new HashSet<>();
            Set<Long> expertiseIds = new HashSet<>();
            Set<Long> unionIds = new HashSet<>();
            Set<Long> teamIds = new HashSet<>();
            Set<Long> employmentTypeIds = new HashSet<>();
            Set<Long> tagIds = new HashSet<>();
            Set<StaffStatusEnum> staffStatuses = new HashSet<>();
            Set<FieldLevelPermission> forOtherFieldLevelPermissions = new HashSet<>();
            for (FieldPermissionQueryResult fieldPermissionQueryResult : longListEntry.getValue()) {
                permissions.addAll(fieldPermissionQueryResult.getPermissions());
                expertiseIds.addAll(fieldPermissionQueryResult.getExpertiseIds());
                unionIds.addAll(fieldPermissionQueryResult.getUnionIds());
                teamIds.addAll(fieldPermissionQueryResult.getTeamIds());
                tagIds.addAll(fieldPermissionQueryResult.getTagIds());
                employmentTypeIds.addAll(fieldPermissionQueryResult.getEmploymentTypeIds());
                staffStatuses.addAll(fieldPermissionQueryResult.getStaffStatuses());
                forOtherFieldLevelPermissions.addAll(fieldPermissionQueryResult.getForOtherFieldLevelPermissions());
            }
            fieldPermissionQueryResults.add(new FieldPermissionQueryResult(longListEntry.getKey(),permissions,expertiseIds,unionIds,teamIds,employmentTypeIds,tagIds,staffStatuses,forOtherFieldLevelPermissions));
        }
        return fieldPermissionQueryResults;
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
            ModelPermissionQueryResult modelPermissionQueryResult = modelPermissionMap.getOrDefault(kPermissionModel.getId(),new ModelPermissionQueryResult(kPermissionModel.getId(),kPermissionModel.getModelName()));
            if (isCollectionNotEmpty(kPermissionModel.getFieldPermissions())) {
                modelPermissionQueryResult.setFieldPermissions(getFieldLevelPermissionQueryResult(fieldLevelPermissionMap,kPermissionModel.getFieldPermissions()));
            }
            if (isCollectionNotEmpty(kPermissionModel.getSubModelPermissions())) {
                modelPermissionQueryResult.setSubModelPermissions(getModelPermissionQueryResults(kPermissionModel.getSubModelPermissions(),modelPermissionMap,fieldLevelPermissionMap,organizationCategory,hubMember));
            }
            modelPermissionQueryResult.setModelName(kPermissionModel.getModelName());
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
            FieldPermissionQueryResult fieldLevelPermissions = fieldLevelPermissionMap.getOrDefault(field.getId(),new FieldPermissionQueryResult(field.getId(),field.getFieldName()));
            fieldLevelPermissions.setFieldName(field.getFieldName());
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
                permissionModelRepository.createAccessGroupPermissionFieldRelationshipType(kPermissionField.getId(), accessGroupIds, fieldDTO.getPermissions(),fieldDTO.getForOtherPermissions().getExpertiseIds(),fieldDTO.getForOtherPermissions().getUnionIds(),fieldDTO.getForOtherPermissions().getTeamIds(),fieldDTO.getForOtherPermissions().getEmploymentTypeIds(),fieldDTO.getForOtherPermissions().getTagIds(),fieldDTO.getForOtherPermissions().getStaffStatuses(),fieldDTO.getForOtherPermissions().getPermissions());
            }
        }
    }

    private void updateModelPermissionOrOrganisationCategory(List<Long> accessGroupIds, boolean updateOrganisationCategories, ModelDTO modelDTO, KPermissionModel kPermissionModel) {
        if(updateOrganisationCategories){
            kPermissionModel.setOrganizationCategories(modelDTO.getOrganizationCategories());
        }else {

            permissionModelRepository.createAccessGroupPermissionModelRelationship(kPermissionModel.getId(), accessGroupIds, modelDTO.getPermissions(),modelDTO.getForOtherPermissions().getExpertiseIds(),modelDTO.getForOtherPermissions().getUnionIds(),modelDTO.getForOtherPermissions().getTeamIds(),modelDTO.getForOtherPermissions().getEmploymentTypeIds(),modelDTO.getForOtherPermissions().getTagIds(),modelDTO.getForOtherPermissions().getStaffStatuses(),modelDTO.getForOtherPermissions().getPermissions());
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
            FieldPermissionHelper fieldPermissionHelper = new FieldPermissionHelper(objects);
            updateObjectsPropertiesBeforeSave(fieldPermissionHelper);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return objects;
    }


    public <T extends UserBaseEntity,E extends UserBaseEntity> void updateObjectsPropertiesBeforeSave(FieldPermissionHelper fieldPermissionHelper){
        for (T object : (List<T>)fieldPermissionHelper.getObjects()) {
            E databaseObject = (E)fieldPermissionHelper.getMapOfDataBaseObject().get(object.getId());
            ObjectMapperUtils.copySpecificPropertiesByMapper(object,databaseObject,fieldPermissionHelper.getPermissionHelper(object.getClass().getSimpleName(),FieldLevelPermission.WRITE));
        }
    }

    public PermissionDefaultDataDTO getDefaultDataOfPermission(Long refrenceId, ConfLevel confLevel) {
        List<Organization> unions;
        OrganizationDTO organizationDTO = null;
        if(ConfLevel.ORGANIZATION.equals(confLevel)){
            organizationDTO = organizationService.getOrganizationWithCountryId(refrenceId);
            unions = organizationService.getAllUnionsByOrganizationOrCountryId(refrenceId, DEFAULT_ID);
        }else {
            unions = organizationService.getAllUnionsByOrganizationOrCountryId(DEFAULT_ID, refrenceId);
        }
        Long countryId = ConfLevel.COUNTRY.equals(confLevel) ? refrenceId : organizationDTO.getCountryId();
        List<ExpertiseDTO> expertises = copyPropertiesOfCollectionByMapper(expertiseGraphRepository.getExpertiesOfCountry(countryId),ExpertiseDTO.class);
        List<EmploymentTypeDTO> employmentTypeDTOS = copyPropertiesOfCollectionByMapper(employmentTypeGraphRepository.getEmploymentTypeByCountry(countryId,false), EmploymentTypeDTO.class);
        List<UnionDTO> unionDTOS = copyPropertiesOfCollectionByMapper(unions,UnionDTO.class);
        List<TeamDTO> teamDTOS = copyPropertiesOfCollectionByMapper(teamGraphRepository.findAllTeamsInOrganization(refrenceId), TeamDTO.class);
        return new PermissionDefaultDataDTO(expertises,unionDTOS,employmentTypeDTOS,teamDTOS,isNull(organizationDTO) ? newArrayList() : organizationDTO.getTagDTOS(),newArrayList(StaffStatusEnum.values()));
    }

    @Getter
    @Setter
    private class FieldPermissionHelper<T extends UserBaseEntity, E extends UserBaseEntity> {
        private List<T> objects;
        private Map<String, ModelDTO> modelMap;
        private Map<Long, E> mapOfDataBaseObject;
        private Map<Long, OtherPermissionDTO> otherPermissionDTOMap;
        private Long currentUserStaffId;
        private boolean hubMember;

        public FieldPermissionHelper(List<T> objects) {
            this.objects = objects;
            Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
            Set<String> modelNames = objects.stream().map(model->model.getClass().getSimpleName()).collect(Collectors.toSet());
            List<AccessGroup> accessGroups =  accessGroupService.validAccessGroupByDate(unitId,getDate());
            hubMember = UserContext.getUserDetails().isHubMember();
            List<ModelPermissionQueryResult> modelPermissionQueryResults = getModelPermission(new ArrayList(modelNames),accessGroups.stream().map(accessGroup -> accessGroup.getId()).collect(Collectors.toSet()),hubMember);
            List<ModelDTO> modelDTOS = copyPropertiesOfCollectionByMapper(modelPermissionQueryResults,ModelDTO.class);
            modelMap = modelDTOS.stream().collect(Collectors.toMap(k -> k.getModelName(), v -> v));
            Map[] mapArray = getObjectByIds(objects);
            mapOfDataBaseObject = mapArray[0];
            otherPermissionDTOMap = mapArray[1];
            Organization parentOrganisation = organizationService.fetchParentOrganization(unitId);
            currentUserStaffId = staffService.getStaffIdByUserId(UserContext.getUserDetails().getId(), parentOrganisation.getId());
        }

        private <ID,E extends UserBaseEntity,T extends UserBaseEntity> Map[] getObjectByIds(List<T> objects){
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
            Map<Long, OtherPermissionDTO> staffPermissionRelatedDataQueryResultMap = staffService.getStaffDataForPermissionByStaffIds((Set<Long>)objectIdsMap.get(Staff.class));
            return new Map[]{mapOfDataBaseObject,staffPermissionRelatedDataQueryResultMap};
        }

        public ObjectMapperUtils.PermissionHelper getPermissionHelper(String className,FieldLevelPermission fieldLevelPermission){
            return new ObjectMapperUtils.PermissionHelper(modelMap.get(className),currentUserStaffId,otherPermissionDTOMap,hubMember,fieldLevelPermission);
        }
    }
}

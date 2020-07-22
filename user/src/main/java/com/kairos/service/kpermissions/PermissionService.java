package com.kairos.service.kpermissions;

import com.kairos.annotations.KPermissionRelatedModel;
import com.kairos.annotations.KPermissionRelationshipFrom;
import com.kairos.annotations.KPermissionRelationshipTo;
import com.kairos.commons.annotation.PermissionClass;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.kpermissions.*;
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
import com.kairos.persistence.model.access_permission.AccessPageQueryResult;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.kpermissions.*;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.repository.custom_repository.CommonRepositoryImpl;
import com.kairos.persistence.repository.kpermissions.PermissionFieldRepository;
import com.kairos.persistence.repository.kpermissions.PermissionModelRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.staff.StaffService;
import com.kairos.utils.PermissionMapperUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.ObjectMapperUtils.copyCollectionPropertiesByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ApplicationConstants.*;
import static com.kairos.constants.CommonConstants.DEFAULT_ID;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_DATANOTFOUND;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_PERMISSION_FIELD;


@Service
public class PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);
    public static final String GET_ID = "getId";

    @Inject
    private PermissionFieldRepository permissionFieldRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject private CommonRepositoryImpl commonRepository;
    @Inject private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject private TeamGraphRepository teamGraphRepository;
    @Inject private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    private static OrganizationService organizationService;
    private static StaffService staffService;
    private static AccessGroupService accessGroupService;
    private static PermissionModelRepository permissionModelRepository;
    @Inject private AccessPageRepository accessPageRepository;
    @Inject private AccessGroupRepository accessGroupRepository;

    @Inject
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }
    @Inject
    public void setStaffService(StaffService staffService) {
        this.staffService = staffService;
    }
    @Inject
    public void setAccessGroupService(AccessGroupService accessGroupService) {
        this.accessGroupService = accessGroupService;
    }
    @Inject
    public void setPermissionModelRepository(PermissionModelRepository permissionModelRepository) {
        this.permissionModelRepository = permissionModelRepository;
    }

    public List<ModelDTO> createPermissionSchema(List<ModelDTO> modelDTOS){
        LOGGER.info("creating model permission");
        Map<String,KPermissionModel> modelNameAndModelMap = StreamSupport.stream(permissionModelRepository.findAll(2).spliterator(), false).filter(it -> !it.isPermissionSubModel()).collect(Collectors.toMap(k->k.getModelName().toLowerCase(),v->v));
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
        kPermissionModels.addAll(copyCollectionPropertiesByMapper(newModelDTO, KPermissionModel.class));
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
            if(!actions.contains(actionDTO.getActionName())){
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
        return copyCollectionPropertiesByMapper(kPermissionModels, ModelDTO.class);
    }

    public Map<String, Object> getPermissionSchema(List<Long> accessGroupIds,Long staffId){
        Map<String, Object> permissionSchemaMap = new HashMap<>();
        List<KPermissionModel> kPermissionModels = getkPermissionModels();
        permissionSchemaMap.put(PERMISSIONS_SCHEMA, copyCollectionPropertiesByMapper(kPermissionModels, ModelDTO.class));
        permissionSchemaMap.put(PERMISSIONS, FieldLevelPermission.values());
        permissionSchemaMap.put(PERMISSION_DATA, copyCollectionPropertiesByMapper(getModelPermission(newArrayList(),accessGroupIds,false,staffId),ModelDTO.class));
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

    private Map[] getMapOfPermission(Collection<Long> accessGroupIds, boolean hubMember, Long staffId) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = permissionModelRepository.getAllModelPermission(accessGroupIds,UserContext.getUserDetails().getLastSelectedOrganizationId(),staffId);
        List<FieldPermissionQueryResult> fieldLevelPermissions = permissionModelRepository.getAllFieldPermission(accessGroupIds,UserContext.getUserDetails().getLastSelectedOrganizationId(),staffId);
        modelPermissionQueryResults = mergeModelPermissionQueryResult(modelPermissionQueryResults);
        fieldLevelPermissions = mergeFieldPermissionQueryResult(fieldLevelPermissions);
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

    private List<ModelPermissionQueryResult> mergeModelPermissionQueryResult(List<ModelPermissionQueryResult> modelPermissions) {
        Map<Long,List<ModelPermissionQueryResult>> groupOfModelPermission = modelPermissions.stream().collect(Collectors.groupingBy(modelPermissionQueryResult -> modelPermissionQueryResult.getId()));
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

    private static List<FieldPermissionQueryResult> mergeFieldPermissionQueryResult(List<FieldPermissionQueryResult> fieldLevelPermissions) {
        Map<Long,List<FieldPermissionQueryResult>> groupOfFieldPermission = fieldLevelPermissions.stream().collect(Collectors.groupingBy(modelPermissionQueryResult -> modelPermissionQueryResult.getId()));
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
            String fieldName = "";
            for (FieldPermissionQueryResult fieldPermissionQueryResult : longListEntry.getValue()) {
                permissions.addAll(fieldPermissionQueryResult.getPermissions());
                expertiseIds.addAll(fieldPermissionQueryResult.getExpertiseIds());
                unionIds.addAll(fieldPermissionQueryResult.getUnionIds());
                teamIds.addAll(fieldPermissionQueryResult.getTeamIds());
                tagIds.addAll(fieldPermissionQueryResult.getTagIds());
                employmentTypeIds.addAll(fieldPermissionQueryResult.getEmploymentTypeIds());
                staffStatuses.addAll(fieldPermissionQueryResult.getStaffStatuses());
                forOtherFieldLevelPermissions.addAll(fieldPermissionQueryResult.getForOtherFieldLevelPermissions());
                fieldName = fieldPermissionQueryResult.getFieldName();
            }
            fieldPermissionQueryResults.add(new FieldPermissionQueryResult(longListEntry.getKey(),permissions,expertiseIds,unionIds,teamIds,employmentTypeIds,tagIds,staffStatuses,forOtherFieldLevelPermissions,fieldName));
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

    public List<ModelPermissionQueryResult> getModelPermission(List<String> modelNames,Collection<Long> accessGroupIds,boolean hubMember,Long staffId){
        Map[] permissionMap = getMapOfPermission(accessGroupIds,hubMember,staffId);
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
    private List<ModelPermissionQueryResult> getModelPermissionQueryResults(List<KPermissionModel> kPermissionModels, Map<Long, ModelPermissionQueryResult> modelPermissionMap, Map<Long, FieldPermissionQueryResult> fieldLevelPermissionMap,OrganizationCategory organizationCategory, boolean hubMember) {
        List<ModelPermissionQueryResult> modelPermissionQueryResults = new ArrayList<>();
        for (KPermissionModel kPermissionModel : kPermissionModels) {
            ModelPermissionQueryResult modelPermissionQueryResult = modelPermissionMap.getOrDefault(kPermissionModel.getId(),new ModelPermissionQueryResult(kPermissionModel.getId(),kPermissionModel.getModelName()));
            if (isCollectionNotEmpty(kPermissionModel.getFieldPermissions())) {
                modelPermissionQueryResult.setFieldPermissions(getFieldLevelPermissionQueryResult(fieldLevelPermissionMap,kPermissionModel.getFieldPermissions(),hubMember));
            }
            if (isCollectionNotEmpty(kPermissionModel.getSubModelPermissions())) {
                modelPermissionQueryResult.setSubModelPermissions(getModelPermissionQueryResults(kPermissionModel.getSubModelPermissions(),modelPermissionMap,fieldLevelPermissionMap,organizationCategory,hubMember));
            }
            modelPermissionQueryResult.setPermissions(hubMember ? newHashSet(FieldLevelPermission.READ,FieldLevelPermission.WRITE) : modelPermissionQueryResult.getPermissions());
            modelPermissionQueryResult.setForOtherFieldLevelPermissions(hubMember ? newHashSet(FieldLevelPermission.READ,FieldLevelPermission.WRITE) : modelPermissionQueryResult.getForOtherFieldLevelPermissions());
            modelPermissionQueryResult.setModelName(kPermissionModel.getModelName());
            modelPermissionQueryResults.add(modelPermissionQueryResult);
        }
        return modelPermissionQueryResults;
    }

    private boolean isValidOrganizationCategory(OrganizationCategory organizationCategory, boolean hubMember, Set<OrganizationCategory> organizationCategories) {
        return hubMember ? hubMember : organizationCategories.contains(organizationCategory);
    }

    private List<FieldPermissionQueryResult> getFieldLevelPermissionQueryResult(Map<Long,FieldPermissionQueryResult> fieldLevelPermissionMap,List<KPermissionField> fields,boolean hubMember){
        List<FieldPermissionQueryResult> fieldPermissionQueryResults = new ArrayList<>();
        for (KPermissionField field : fields) {
            FieldPermissionQueryResult fieldLevelPermissions = fieldLevelPermissionMap.getOrDefault(field.getId(),new FieldPermissionQueryResult(field.getId(),field.getFieldName()));
            fieldLevelPermissions.setFieldName(field.getFieldName());
            fieldLevelPermissions.setPermissions(hubMember ? newHashSet(FieldLevelPermission.READ,FieldLevelPermission.WRITE) : fieldLevelPermissions.getPermissions());
            fieldLevelPermissions.setForOtherFieldLevelPermissions(hubMember ? newHashSet(FieldLevelPermission.READ,FieldLevelPermission.WRITE) : fieldLevelPermissions.getForOtherFieldLevelPermissions());
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
            if(isNotNull(kPermissionModel)){
                updateModelPermissionOrOrganisationCategory(accessGroupIds, updateOrganisationCategories, modelPermissionDTO, kPermissionModel);
            }
            if(!modelPermissionDTO.getSubModelPermissions().isEmpty()){
                updateOrganisationCategoryOrPermissions(modelPermissionDTO.getSubModelPermissions(), accessGroupIds,updateOrganisationCategories);
            }
        });
    }

    private void updatePermissionOrOrganisationCategory(List<Long> accessGroupIds, boolean updateOrganisationCategories, FieldDTO fieldDTO, KPermissionField kPermissionField) {
        if(updateOrganisationCategories){
            kPermissionField.setOrganizationCategories(fieldDTO.getOrganizationCategories());
        }else {
                if (kPermissionField == null) {
                    exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_PERMISSION_FIELD, fieldDTO.getId());
                } else {
                    permissionModelRepository.createAccessGroupPermissionFieldRelationshipType(kPermissionField.getId(), accessGroupIds, fieldDTO.getPermissions(), fieldDTO.getForOtherPermissions().getExpertiseIds(), fieldDTO.getForOtherPermissions().getUnionIds(), fieldDTO.getForOtherPermissions().getTeamIds(), fieldDTO.getForOtherPermissions().getEmploymentTypeIds(), fieldDTO.getForOtherPermissions().getTagIds(), fieldDTO.getForOtherPermissions().getStaffStatuses(), fieldDTO.getForOtherPermissions().getPermissions());
                }
            }
    }

    private void updateModelPermissionOrOrganisationCategory(List<Long> accessGroupIds, boolean updateOrganisationCategories, ModelDTO modelDTO, KPermissionModel kPermissionModel) {
           if (updateOrganisationCategories) {
               kPermissionModel.setOrganizationCategories(modelDTO.getOrganizationCategories());
           } else {
               permissionModelRepository.createAccessGroupPermissionModelRelationship(kPermissionModel.getId(), accessGroupIds, modelDTO.getPermissions(), modelDTO.getForOtherPermissions().getExpertiseIds(), modelDTO.getForOtherPermissions().getUnionIds(), modelDTO.getForOtherPermissions().getTeamIds(), modelDTO.getForOtherPermissions().getEmploymentTypeIds(), modelDTO.getForOtherPermissions().getTagIds(), modelDTO.getForOtherPermissions().getStaffStatuses(), modelDTO.getForOtherPermissions().getPermissions());
           }
    }

    private KPermissionFieldQueryResult getkPermissionFieldQueryResult(ModelDTO modelDTO, FieldDTO fieldDTO) {
        KPermissionFieldQueryResult kPermissionFieldQueryResult = permissionFieldRepository.getPermissionFieldByIdAndPermissionModelId(modelDTO.getId(), fieldDTO.getId());
        if (kPermissionFieldQueryResult == null) {
            exceptionService.dataNotFoundByIdException("message.permission.KPermissionFieldQueryResult");
        }
        return kPermissionFieldQueryResult;
    }

    public <T,E extends UserBaseEntity> void updateModelBasisOfPermission(List<T> objects,Set<FieldLevelPermission> fieldLevelPermissions){
        try {
            if(UserContext.getUserDetails().isHubMember()){
                return;
            }
            FieldPermissionHelper fieldPermissionHelper = new FieldPermissionHelper(objects,fieldLevelPermissions);
            updateObjectsPropertiesBeforeSave(fieldPermissionHelper,fieldLevelPermissions);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }


    public <T extends UserBaseEntity,E extends UserBaseEntity> void updateObjectsPropertiesBeforeSave(FieldPermissionHelper fieldPermissionHelper,Set<FieldLevelPermission> fieldLevelPermissions){
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

    public PermissionDefaultDataDTO getDefaultDataOfPermission(Long refrenceId, ConfLevel confLevel) {
        List<Organization> unions;
        OrganizationDTO organizationDTO = null;
        if(ConfLevel.ORGANIZATION.equals(confLevel)){
            Organization organization=organizationService.fetchParentOrganization(refrenceId);
            organizationDTO = organizationService.getOrganizationWithCountryId(refrenceId);
            unions = organizationService.getAllUnionsByOrganizationOrCountryId(organization.getId(), DEFAULT_ID);
        }else {
            unions = organizationService.getAllUnionsByOrganizationOrCountryId(DEFAULT_ID, refrenceId);
        }
        Long countryId = ConfLevel.COUNTRY.equals(confLevel) ? refrenceId : organizationDTO.getCountryId();
        List<ExpertiseDTO> expertises = copyCollectionPropertiesByMapper(expertiseGraphRepository.getExpertiesOfCountry(countryId),ExpertiseDTO.class);
        List<EmploymentTypeDTO> employmentTypeDTOS = copyCollectionPropertiesByMapper(employmentTypeGraphRepository.getEmploymentTypeByCountry(countryId,false), EmploymentTypeDTO.class);
        List<UnionDTO> unionDTOS = copyCollectionPropertiesByMapper(unions,UnionDTO.class);
        List<TeamDTO> teamDTOS = copyCollectionPropertiesByMapper(teamGraphRepository.findAllTeamsInOrganization(refrenceId), TeamDTO.class);
        return new PermissionDefaultDataDTO(expertises,unionDTOS,employmentTypeDTOS,teamDTOS,isNull(organizationDTO) ? newArrayList() : organizationDTO.getTagDTOS(),newArrayList(StaffStatusEnum.values()));
    }

    public static <T extends UserBaseEntity> T checkAndUpdateRelationShipPermission(T entity){
        if(isNotNull(entity) && !UserContext.getUserDetails().isHubMember()){
            Object[] modelNameAndFieldName = getRelationShipModelPermissionModelName(entity);
            Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
            List<AccessGroup> accessGroups =  accessGroupService.validAccessGroupByDate(unitId,getDate());
            Organization parentOrganisation = organizationService.fetchParentOrganization(unitId);
            Long currentUserStaffId = staffService.getStaffIdByUserId(UserContext.getUserDetails().getId(), parentOrganisation.getId());
            Set<Long> accessGroupIds = accessGroups.stream().map(UserBaseEntity::getId).collect(Collectors.toSet());
            List<FieldPermissionQueryResult> fieldLevelPermissions = permissionModelRepository.getAllFieldPermissionByFieldNames((String)modelNameAndFieldName[0],newHashSet((String) modelNameAndFieldName[1]),accessGroupIds);
            fieldLevelPermissions = mergeFieldPermissionQueryResult(fieldLevelPermissions);
            Map<String,FieldPermissionQueryResult> fieldPermissionQueryResultMap = fieldLevelPermissions.stream().collect(Collectors.toMap(k->k.getFieldName(),v->v));
            entity = validateAndGetEntity(entity, modelNameAndFieldName, currentUserStaffId, fieldPermissionQueryResultMap);
        }
        return entity;
    }

    private static <T extends UserBaseEntity> T validateAndGetEntity(T entity, Object[] modelNameAndFieldName, Long currentUserStaffId, Map<String, FieldPermissionQueryResult> fieldPermissionQueryResultMap) {
        if(fieldPermissionQueryResultMap.containsKey(modelNameAndFieldName[1])){
            FieldPermissionQueryResult fieldPermissionQueryResult = fieldPermissionQueryResultMap.get(modelNameAndFieldName[1]);
            Long entityStaffId = (Long) modelNameAndFieldName[2];
            if(currentUserStaffId.equals(entityStaffId)){
                if(!fieldPermissionQueryResult.getPermissions().contains(FieldLevelPermission.WRITE)){
                    entity = null;
                }
            }else {
                Map<Long, OtherPermissionDTO> staffPermissionRelatedDataQueryResultMap = staffService.getStaffDataForPermissionByStaffIds(newHashSet(entityStaffId));
                if(!(fieldPermissionQueryResult.getForOtherPermissions().isValid(staffPermissionRelatedDataQueryResultMap.get(entityStaffId)) && fieldPermissionQueryResult.getForOtherPermissions().getPermissions().contains(FieldLevelPermission.WRITE))){
                    entity = null;
                }
            }
        }
        return entity;
    }

    private static <T,S> Object[] getRelationShipModelPermissionModelName(T entity) {
        String modelName = null;
        String fieldName = null;
        Long id = 0l;
        for (Field field : entity.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(KPermissionRelationshipFrom.class)){
                try {
                    field.setAccessible(true);
                    S startNode = (S)field.get(entity);
                    id = (Long) startNode.getClass().getMethod(GET_ID).invoke(startNode);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                modelName = field.getType().getSimpleName();
            }
            if(field.isAnnotationPresent(KPermissionRelationshipTo.class)){
                fieldName = field.getName();
            }
        }
        return new Object[]{modelName,fieldName,id};
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
        private Long staffId;

        public FieldPermissionHelper(List<T> objects,Set<FieldLevelPermission> fieldLevelPermissions) {
            this.objects = objects;
            Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
            Set<String> modelNames = getModelNames(objects);
            List<AccessGroup> accessGroups =  accessGroupService.validAccessGroupByDate(unitId,getDate());
            hubMember = UserContext.getUserDetails().isHubMember();
            List<ModelPermissionQueryResult> modelPermissionQueryResults = getModelPermission(new ArrayList(modelNames),accessGroups.stream().map(accessGroup -> accessGroup.getId()).collect(Collectors.toSet()),hubMember,currentUserStaffId);
            List<ModelDTO> modelDTOS = copyCollectionPropertiesByMapper(modelPermissionQueryResults,ModelDTO.class);
            modelMap = modelDTOS.stream().collect(Collectors.toMap(k -> k.getModelName(), v -> v));
            Map[] mapArray = getObjectByIds(objects,fieldLevelPermissions);
            mapOfDataBaseObject = mapArray[0];
            otherPermissionDTOMap = mapArray[1];
            Organization parentOrganisation = organizationService.fetchParentOrganization(unitId);
            currentUserStaffId = staffService.getStaffIdByUserId(UserContext.getUserDetails().getId(), parentOrganisation.getId());
        }

        private Set<String> getModelNames(List<T> objects) {
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

        private <ID,E,T> Map[] getObjectByIds(List<T> objects,Set<FieldLevelPermission> fieldLevelPermissions){
            Map<Class,Set<ID>> objectIdsMap = new HashMap<>();
            for (T object : objects) {
                if(!object.getClass().isAnnotationPresent(KPermissionRelatedModel.class)){
                    try {
                        ID id = (ID) object.getClass().getMethod(GET_ID).invoke(object);
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
                getMapOfDataBaseObjects(objectIdsMap, mapOfDataBaseObject);
            }
            Map<Long, OtherPermissionDTO> staffPermissionRelatedDataQueryResultMap = new HashMap<>();
            if(objectIdsMap.containsKey(Staff.class)){
                 staffPermissionRelatedDataQueryResultMap = staffService.getStaffDataForPermissionByStaffIds((Set<Long>)objectIdsMap.get(Staff.class));
            }
            return new Map[]{mapOfDataBaseObject,staffPermissionRelatedDataQueryResultMap};
        }

        private <ID, E> void getMapOfDataBaseObjects(Map<Class, Set<ID>> objectIdsMap, Map<ID, E> mapOfDataBaseObject) {
            for (Map.Entry<Class, Set<ID>> classIdSetEntry : objectIdsMap.entrySet()) {
                Collection<E> databaseObject = commonRepository.findByIds(classIdSetEntry.getKey(),(Collection<? extends Serializable>) classIdSetEntry.getValue(),2);
                for (E object : databaseObject) {
                    try {
                        ID id = (ID) object.getClass().getMethod(GET_ID).invoke(object);
                        mapOfDataBaseObject.put(id,object);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public PermissionMapperUtils.PermissionHelper getPermissionHelper(String className,Set<FieldLevelPermission> fieldLevelPermissions){
            return new PermissionMapperUtils.PermissionHelper(modelMap.get(className),currentUserStaffId,otherPermissionDTOMap,hubMember,fieldLevelPermissions);
        }
    }

    public <T> FieldPermissionUserData fetchPermissions(Set<String> modelNames, Long unitId){
        List<AccessGroup> accessGroups =  accessGroupService.validAccessGroupByDate(unitId,getDate());
        boolean hubMember = UserContext.getUserDetails().isHubMember();
        List<ModelPermissionQueryResult> modelPermissionQueryResults = getModelPermission(new ArrayList(modelNames),accessGroups.stream().map(accessGroup -> accessGroup.getId()).collect(Collectors.toSet()),hubMember,null);
        List<ModelDTO> modelDTOS = copyCollectionPropertiesByMapper(modelPermissionQueryResults,ModelDTO.class);
        Organization parentOrganisation=organizationService.fetchParentOrganization(unitId);
        Long currentUserStaffId = staffService.getStaffIdByUserId(UserContext.getUserDetails().getId(), parentOrganisation.getId());
        return new FieldPermissionUserData(modelDTOS,currentUserStaffId);
    }

    public void assignPermission(Long unitId,Long accessGroupId, CustomPermissionDTO customPermissionDTO) {
        Set<Long> kPermissionModelIds=permissionModelRepository.kPermissionModelIds(customPermissionDTO.getId());
        OtherPermissionDTO forOtherPermissions=customPermissionDTO.getForOtherPermissions();
        LOGGER.info("other permissions are {}",customPermissionDTO.getForOtherPermissions().toString());
        accessGroupRepository.setCustomPermissionForSubModelAndFields(customPermissionDTO.getStaffId(), unitId, accessGroupId,kPermissionModelIds, customPermissionDTO.getPermissions(),forOtherPermissions.getExpertiseIds(),forOtherPermissions.getUnionIds(),forOtherPermissions.getTeamIds(),
                forOtherPermissions.getEmploymentTypeIds(),forOtherPermissions.getTagIds(),forOtherPermissions.getStaffStatuses(),forOtherPermissions.getPermissions());
    }

    private Set<Long> getAllIdsToSetPermissions(KPermissionModel kPermissionModel,Set<Long> kPermissionModelIds) {
        kPermissionModelIds.add(kPermissionModel.getId());
        kPermissionModelIds.addAll(kPermissionModel.getFieldPermissions().stream().map(UserBaseEntity::getId).collect(Collectors.toSet()));
        kPermissionModel.getSubModelPermissions().forEach(subModel->{
            getAllIdsToSetPermissions(subModel,kPermissionModelIds);
        });
        return kPermissionModelIds;
    }

}

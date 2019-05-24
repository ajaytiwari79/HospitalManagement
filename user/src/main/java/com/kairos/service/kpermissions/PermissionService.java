package com.kairos.service.kpermissions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.kpermissions.*;
import com.kairos.enums.kpermissions.FieldLevelPermissions;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.kpermissions.KPermissionField;
import com.kairos.persistence.model.kpermissions.KPermissionFieldQueryResult;
import com.kairos.persistence.model.kpermissions.KPermissionModel;
import com.kairos.persistence.model.kpermissions.KPermissionSubModelFieldQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.repository.kpermissions.AccessGroupPermissionFieldRelationshipGraphRepository;
import com.kairos.persistence.repository.kpermissions.AccessGroupPermissionModelRelationshipGraphRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public boolean createPermissionSchema(List<ModelDTO> modelDTOS){
        List<KPermissionModel> kPermissionModels = new ArrayList<>();
        permissionModelRepository.findAll().iterator().forEachRemaining(kPermissionModels::add);
        kPermissionModels = kPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        buildPermissionModelData(modelDTOS, kPermissionModels, false);
        permissionModelRepository.save(kPermissionModels,2);
        return true;
    }

    private List<KPermissionModel> buildPermissionModelData(List<ModelDTO> modelDTOS, List<KPermissionModel> KPermissionModelList, boolean isSubModel){
        List<ModelDTO> newModelDTO = new ArrayList<>();
        List<KPermissionModel> KPermissionModels = new ArrayList<>();
        modelDTOS.forEach(modelDTO -> {
            Optional<KPermissionModel>  permissionModelObj = KPermissionModelList.stream().filter(permissionModel ->
                    modelDTO.getModelName().equalsIgnoreCase(permissionModel.getModelName())
            ).findAny();
            if(permissionModelObj.isPresent()){
                KPermissionModel KPermissionModel = permissionModelObj.get();
                List<String> fields = KPermissionModel.getFields().stream().map(KPermissionField::getFieldName).collect(Collectors.toList());
                modelDTO.getFields().forEach(fieldDTO -> {
                    if(!fields.contains(fieldDTO.getFieldName())){
                        KPermissionModel.getFields().add(new KPermissionField(fieldDTO.getFieldName()));
                    }
                });
                KPermissionModel.setPermissionSubModel(isSubModel);
                if(!modelDTO.getSubModels().isEmpty()){
                    KPermissionModel.getSubModels().addAll(buildPermissionModelData(modelDTO.getSubModels(), KPermissionModel.getSubModels(), true));
                }
                    KPermissionModels.add(KPermissionModel);
            }else{
                newModelDTO.add(modelDTO);
            }

        });
        KPermissionModelList.addAll(ObjectMapperUtils.copyPropertiesOfListByMapper(newModelDTO, KPermissionModel.class));
        return KPermissionModelList;
    }

    public List<ModelDTO> getPermissionSchema(){
        List<KPermissionModel> KPermissionModels = new ArrayList();
        permissionModelRepository.findAll().iterator().forEachRemaining(KPermissionModels::add);
        KPermissionModels = KPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        return ObjectMapperUtils.copyPropertiesOfListByMapper(KPermissionModels, ModelDTO.class);
    }

    public List<PermissionDTO> createPermissions(List<PermissionDTO> permissionDTOList){
        permissionDTOList.forEach(permissionDTO -> {
            List<AccessGroup> accessGroups = accessGroupRepository.findAllById(permissionDTO.getAccessGroupIds());
            linkAccessGroupToSubModelAndPermissionFields(permissionDTO.getModelPermissions(), accessGroups);
        });
        return permissionDTOList;
    }


    public void linkAccessGroupToSubModelAndPermissionFields(List<ModelPermissionDTO> modelPermissionDTOS, List<AccessGroup> accessGroups){
        modelPermissionDTOS.forEach(modelPermissionDTO -> {
            KPermissionModel KPermissionModel = null;
            for(FieldPermissionDTO fieldPermissionDTO : modelPermissionDTO.getFieldPermissions()){
                KPermissionFieldQueryResult KPermissionFieldQueryResult = permissionFieldRepository.getPermissionFieldByIdAndPermissionModelId(modelPermissionDTO.getPermissionModelId(), fieldPermissionDTO.getFieldId());
                if(KPermissionFieldQueryResult == null){
                    exceptionService.dataNotFoundByIdException("message.permission.KPermissionFieldQueryResult");
                }
                KPermissionModel = KPermissionFieldQueryResult.getKPermissionModel();
                KPermissionField KPermissionField = KPermissionFieldQueryResult.getKPermissionField();
                if(KPermissionField == null){
                    exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_PERMISSION_FIELD, fieldPermissionDTO.getFieldId());
                }else{
                    accessGroups.forEach(accessGroup -> {
                        accessGroupPermissionFieldRelationshipGraphRepository.createAccessGroupPermissionFieldRelationshipType(KPermissionField.getId(),accessGroup.getId(),FieldLevelPermissions.getByValue(fieldPermissionDTO.getFieldPermission()));
                    });
                }
            }
            for(AccessGroup accessGroup : accessGroups){
                accessGroupPermissionModelRelationshipGraphRepository.createAccessGroupPermissionModelRelationship(KPermissionModel.getId(), accessGroup.getId(),FieldLevelPermissions.getByValue(modelPermissionDTO.getModelPermission()));
            }
            if(!modelPermissionDTO.getSubModelPermissions().isEmpty()){
                linkAccessGroupToSubModelAndPermissionFields(modelPermissionDTO.getSubModelPermissions(), accessGroups);
            }
        });
    }

    public <E extends Object, T extends UserBaseEntity> E evaluatePermission(@Valid E dtoObject, Class<T> modelClass, List<FieldLevelPermissions> permissions){
        try {

            Organization organization = organizationService.fetchParentOrganization(UserContext.getUnitId());
            Long staffId = staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), organization.getId());
            StaffAccessGroupQueryResult staffAccessGroupQueryResult =  accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId, organization.getId());
            List<Long> accessGroupIds = staffAccessGroupQueryResult.getAccessGroupIds();
            //List<String> permissionFields = permissionFieldRepository.findPermissionFieldsByAccessGroupAndModelClass(modelClass.toString(), accessGroupIds,permissions);
            //System.out.println(permissionFields);
            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
            filterProvider.addFilter("permissionValidatorFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("firstName"));
            ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();
            objectMapper.setFilterProvider(filterProvider);
            String jsonData = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(dtoObject);
            System.out.println(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T extends UserBaseEntity> List<KPermissionModelFieldDTO> fetchPermissionFields(Class<T> modelClass, List<FieldLevelPermissions> permissions){
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

package com.kairos.service.kpermissions;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.kpermissions.FieldPermissionDTO;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.ModelPermissionDTO;
import com.kairos.dto.kpermissions.PermissionDTO;
import com.kairos.enums.kpermissions.FieldLevelPermissions;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.kpermissions.*;
import com.kairos.persistence.repository.kpermissions.AccessGroupPermissionFieldRelationshipGraphRepository;
import com.kairos.persistence.repository.kpermissions.AccessGroupPermissionModelRelationshipGraphRepository;
import com.kairos.persistence.repository.kpermissions.PermissionFieldRepository;
import com.kairos.persistence.repository.kpermissions.PermissionModelRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public boolean createPermissionSchema(List<ModelDTO> modelDTOS){
        List<KPermissionModel> KPermissionModels = new ArrayList<>();
        permissionModelRepository.findAll().iterator().forEachRemaining(KPermissionModels::add);
        KPermissionModels = KPermissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        buildPermissionModelData(modelDTOS, KPermissionModels, false);
        permissionModelRepository.save(KPermissionModels,2);
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
        List<AccessGroupPermissionFieldRelationshipType> accessGroupPermissionFieldRelationshipTypes = new ArrayList<>();
        List<AccessGroupPermissionModelRelationshipType> accessGroupPermissionModelRelationshipTypes = new ArrayList<>();
        modelPermissionDTOS.forEach(modelPermissionDTO -> {
            KPermissionModel KPermissionModel = null;
            for(FieldPermissionDTO fieldPermissionDTO : modelPermissionDTO.getFieldPermissions()){
                KPermissionFieldQueryResult KPermissionFieldQueryResult = permissionFieldRepository.getPermissionFieldByIdAndPermissionModelId(modelPermissionDTO.getPermissionModelId(), fieldPermissionDTO.getFieldId());
                KPermissionModel = KPermissionFieldQueryResult.getKPermissionModel();
                KPermissionField KPermissionField = KPermissionFieldQueryResult.getKPermissionField();
                if(KPermissionField == null){
                    exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.permission.field", fieldPermissionDTO.getFieldId());
                }else{
                    accessGroups.forEach(accessGroup -> {
                        accessGroupPermissionFieldRelationshipTypes.add(new AccessGroupPermissionFieldRelationshipType(KPermissionField, accessGroup, FieldLevelPermissions.getByValue(fieldPermissionDTO.getFieldPermission())));
                    });
                }
            }
            for(AccessGroup accessGroup : accessGroups){
                accessGroupPermissionModelRelationshipTypes.add(new AccessGroupPermissionModelRelationshipType(KPermissionModel,accessGroup,FieldLevelPermissions.getByValue(modelPermissionDTO.getModelPermission())));
            }
            if(!modelPermissionDTO.getSubModelPermissions().isEmpty()){
                linkAccessGroupToSubModelAndPermissionFields(modelPermissionDTO.getSubModelPermissions(), accessGroups);
            }
        });
        accessGroupPermissionFieldRelationshipGraphRepository.saveAll(accessGroupPermissionFieldRelationshipTypes);
        accessGroupPermissionModelRelationshipGraphRepository.saveAll(accessGroupPermissionModelRelationshipTypes);
    }
}

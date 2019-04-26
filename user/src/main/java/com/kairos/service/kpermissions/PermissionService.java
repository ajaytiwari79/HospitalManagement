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
        List<PermissionModel> permissionModels = new ArrayList<>();
        permissionModelRepository.findAll().iterator().forEachRemaining(permissionModels::add);
        permissionModels = permissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        buildPermissionModelData(modelDTOS, permissionModels, false);
        permissionModelRepository.save(permissionModels,2);
        return true;
    }

    private List<PermissionModel> buildPermissionModelData(List<ModelDTO> modelDTOS,List<PermissionModel> permissionModelList, boolean isSubModel){
        List<ModelDTO> newModelDTO = new ArrayList<>();
        List<PermissionModel> permissionModels = new ArrayList<>();
        modelDTOS.forEach(modelDTO -> {
            Optional<PermissionModel>  permissionModelObj = permissionModelList.stream().filter(permissionModel ->
                    modelDTO.getModelName().equalsIgnoreCase(permissionModel.getModelName())
            ).findAny();
            if(permissionModelObj.isPresent()){
                PermissionModel permissionModel = permissionModelObj.get();
                List<String> fields = permissionModel.getFields().stream().map(PermissionField::getFieldName).collect(Collectors.toList());
                modelDTO.getFields().forEach(fieldDTO -> {
                    if(!fields.contains(fieldDTO.getFieldName())){
                        permissionModel.getFields().add(new PermissionField(fieldDTO.getFieldName()));
                    }
                });
                permissionModel.setPermissionSubModel(isSubModel);
                List<ModelDTO> subModels = modelDTO.getSubModels();
                if(!subModels.isEmpty()){
                    permissionModel.getSubModels().addAll(buildPermissionModelData(subModels, permissionModel.getSubModels(), true));
                }
                    permissionModels.add(permissionModel);
            }else{
                newModelDTO.add(modelDTO);
            }

        });
        permissionModelList.addAll(ObjectMapperUtils.copyPropertiesOfListByMapper(newModelDTO, PermissionModel.class));
        return permissionModelList;
    }

    public List<ModelDTO> getPermissionSchema(){
        List<PermissionModel> permissionModels = new ArrayList();
        permissionModelRepository.findAll().iterator().forEachRemaining(permissionModels::add);
        permissionModels = permissionModels.stream().filter(it -> !it.isPermissionSubModel()).collect(Collectors.toList());
        return ObjectMapperUtils.copyPropertiesOfListByMapper(permissionModels, ModelDTO.class);
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
            PermissionModel permissionModel = null;
            for(FieldPermissionDTO fieldPermissionDTO : modelPermissionDTO.getFieldPermissions()){
                PermissionFieldQueryResult permissionFieldQueryResult= permissionFieldRepository.getPermissionFieldByIdAndPermissionModelId(modelPermissionDTO.getPermissionModelId(), fieldPermissionDTO.getFieldId());
                permissionModel= permissionFieldQueryResult.getPermissionModel();
                PermissionField permissionField=permissionFieldQueryResult.getPermissionField();
                if(permissionField == null){
                    exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.permission.field", fieldPermissionDTO.getFieldId());
                }else{
                    accessGroups.forEach(accessGroup -> {
                        accessGroupPermissionFieldRelationshipTypes.add(new AccessGroupPermissionFieldRelationshipType(permissionField, accessGroup, FieldLevelPermissions.getByValue(fieldPermissionDTO.getFieldPermission())));
                    });
                }
            }
            for(AccessGroup accessGroup : accessGroups){
                accessGroupPermissionModelRelationshipTypes.add(new AccessGroupPermissionModelRelationshipType(permissionModel,accessGroup,FieldLevelPermissions.getByValue(modelPermissionDTO.getModelPermission())));
            }
            if(!modelPermissionDTO.getSubModelPermissions().isEmpty()){
                linkAccessGroupToSubModelAndPermissionFields(modelPermissionDTO.getSubModelPermissions(), accessGroups);
            }
        });
        accessGroupPermissionFieldRelationshipGraphRepository.saveAll(accessGroupPermissionFieldRelationshipTypes);
        accessGroupPermissionModelRelationshipGraphRepository.saveAll(accessGroupPermissionModelRelationshipTypes);
    }
}

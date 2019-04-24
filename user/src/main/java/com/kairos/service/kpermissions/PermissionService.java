package com.kairos.service.kpermissions;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.persistence.model.kpermissions.PermissionField;
import com.kairos.persistence.model.kpermissions.PermissionModel;
import com.kairos.persistence.repository.kpermissions.PermissionModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    @Inject
    private PermissionModelRepository permissionModelRepository;

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

    public List<Map<String, Object>> getPermissionSchema(){
        Iterable<PermissionModel> modelData = permissionModelRepository.findAll(2);
        return null;
    }
}

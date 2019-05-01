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
import java.util.*;

import static com.kairos.constants.ApplicationConstants.*;

@Service
public class PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    @Inject
    private PermissionModelRepository permissionModelRepository;

    public Boolean createPermissionSchema(List<ModelDTO> modelDTOS){
        List<Map<String, Object>> modelData = permissionModelRepository.getPermissionModelWithModelAndFields();
        Set<ModelDTO> newModelDTO = new HashSet<>();
        Set<PermissionModel> permissionModels = new HashSet<>();
            modelDTOS.forEach(modelDTO -> {
                Optional<Map<String, Object>>  modelMap = modelData.stream().filter(modelDataMap ->
                    modelDTO.getModelName().equalsIgnoreCase((String)modelDataMap.get(MODEL_NAME))
                ).findAny();
                if(modelMap.isPresent()){
                    Map<String, Object> modelMapObj = modelMap.get();
                    PermissionModel permissionModel = (PermissionModel)modelMapObj.get(MODEL);
                    List<String> fields = Arrays.asList((String[])modelMapObj.get(FIELDS));
                    modelDTO.getFields().forEach(fieldDTO -> {
                        if(!fields.contains(fieldDTO.getFieldName())){
                            permissionModel.getFields().add(new PermissionField(fieldDTO.getFieldName()));
                        }
                    });
                    permissionModels.add(permissionModel);
                }else{
                    newModelDTO.add(modelDTO);
                }

                });

        permissionModels.addAll(ObjectMapperUtils.copyPropertiesOfSetByMapper(newModelDTO, PermissionModel.class));
        permissionModelRepository.saveAll(permissionModels);
        return true;
    }

    public List<Map<String, Object>> getPermissionSchema(){
        List<Map<String, Object>> modelData = permissionModelRepository.getPermissionModelDataWithFields();
        return modelData;
    }
}

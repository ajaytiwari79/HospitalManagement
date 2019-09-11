package com.kairos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.service.kpermissions.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PermissionSchemaProcessor  implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionSchemaProcessor.class);

    private PermissionService permissionService;

    private ObjectMapper objectMapper;

    public PermissionSchemaProcessor(List<Map<String, Object>> data, PermissionService permissionService, ObjectMapper objectMapper) {
       this.permissionService = permissionService;
       this.objectMapper = objectMapper;
       createPermissionSchema(data);
    }

    private void createPermissionSchema(List<Map<String, Object>> data){
        List<ModelDTO> modelDTOList = new ArrayList<>();
        for(Map<String, Object> modelMap : data){
            modelDTOList.add(objectMapper.convertValue(modelMap, ModelDTO.class));
        }
        if(!modelDTOList.isEmpty()) {
            permissionService.createPermissionSchema(modelDTOList);
        }
    }




}

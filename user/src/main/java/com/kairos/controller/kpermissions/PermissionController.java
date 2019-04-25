package com.kairos.controller.kpermissions;

import com.kairos.dto.ValidateRequestBodyList;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.PermissionDTO;
import com.kairos.service.kpermissions.PermissionService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

import static com.kairos.constants.ApiConstants.API_V1;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class PermissionController {

    @Inject
    private PermissionService permissionService;

    @RequestMapping(value = "/create_permission_schema",method = RequestMethod.POST)
    public ResponseEntity createFLPSchema(@Valid @RequestBody List<ModelDTO> modelDTO)  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.createPermissionSchema(modelDTO));

    }

    @RequestMapping(value = "/get_permission_schema",method = RequestMethod.GET)
    public ResponseEntity getFLPSchema()  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionSchema());

    }


    @RequestMapping(value = "/create_permission",method = RequestMethod.POST)
    public ResponseEntity createFieldPermissions(@Valid @RequestBody ValidateRequestBodyList<PermissionDTO> permissionDTO)  {
        if (CollectionUtils.isEmpty(permissionDTO.getRequestBody())) {
            return ResponseHandler.generateResponseDTO(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.createPermissions(permissionDTO.getRequestBody()));

    }

}

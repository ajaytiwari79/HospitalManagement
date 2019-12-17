package com.kairos.controller.kpermissions;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.PermissionDTO;
import com.kairos.service.kpermissions.PermissionService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/access_group_permissions",method = RequestMethod.GET)
    public ResponseEntity getAccessGroupPermissions(@RequestParam List<Long> accessGroupIds )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionSchema(accessGroupIds));

    }


    @PutMapping(value = "/update_permission")
    public ResponseEntity createFieldPermissions(@Valid @RequestBody PermissionDTO permissionDTO,@RequestParam boolean updateOrganisationCategories)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.createPermissions(permissionDTO,updateOrganisationCategories));

    }

    @GetMapping(value = "/get_default_data_of_permission")
    public ResponseEntity getDefaultDataOfPermission(@RequestParam Long referenceId, @RequestParam ConfLevel confLevel)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getDefaultDataOfPermission(referenceId,confLevel));

    }

}

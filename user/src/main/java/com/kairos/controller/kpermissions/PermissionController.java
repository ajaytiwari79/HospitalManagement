package com.kairos.controller.kpermissions;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.kpermissions.CustomPermissionDTO;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.UNIT_URL;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class PermissionController {

    @Inject
    private PermissionService permissionService;

    @PostMapping(value = "/create_permission_schema")
    public ResponseEntity createFLPSchema(@Valid @RequestBody List<ModelDTO> modelDTO)  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.createPermissionSchema(modelDTO));

    }

    @RequestMapping(value = "/get_permission_schema",method = RequestMethod.GET)
    public ResponseEntity getFLPSchema()  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionSchema());

    }

    @RequestMapping(value = "/access_group_permissions",method = RequestMethod.GET)
    public ResponseEntity getAccessGroupPermissions(@RequestParam Set<Long> accessGroupIds )  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionSchema(accessGroupIds,null));

    }


    @PutMapping(value = "/update_permission")
    public ResponseEntity createFieldPermissions(@Valid @RequestBody PermissionDTO permissionDTO,@RequestParam boolean updateOrganisationCategories)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.createPermissions(permissionDTO,updateOrganisationCategories));

    }

    @GetMapping(value = "/get_default_data_of_permission")
    public ResponseEntity getDefaultDataOfPermission(@RequestParam Long referenceId, @RequestParam ConfLevel confLevel)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getDefaultDataOfPermission(referenceId,confLevel));

    }

    @PutMapping(value = "/validate_permission")
    public ResponseEntity validatePermission(@RequestParam Long referenceId, @RequestParam ConfLevel confLevel)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getDefaultDataOfPermission(referenceId,confLevel));

    }

    @PostMapping(value = "/unit/{unitId}/fetch_permissions")
    public ResponseEntity fetchPermission(@RequestBody Set<String> objects, @PathVariable Long unitId)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.fetchPermissions(objects,unitId));
    }

    @PostMapping(value = UNIT_URL+"/access_group/{accessGroupId}/auth/field_level_permission")
    public ResponseEntity<Map<String, Object>> assignPermissionToModel(@PathVariable Long unitId,@PathVariable Long accessGroupId, @RequestBody CustomPermissionDTO customPermissionDTO) {
        permissionService.assignPermission(unitId,accessGroupId,customPermissionDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @GetMapping(value = UNIT_URL+"/access_group/{accessGroupId}/auth/field_level_permission")
    public ResponseEntity<Map<String, Object>> getAccessPageByAccessGroup(@RequestParam("staffId") Long staffId, @PathVariable Long accessGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionSchema(newHashSet(accessGroupId),staffId));
    }
}

package com.kairos.controller.kpermissions;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.kpermissions.CustomPermissionDTO;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.PermissionDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpermissions.PermissionAction;
import com.kairos.persistence.model.kpermissions.KPermissionAction;
import com.kairos.service.kpermissions.PermissionService;
import com.kairos.service.translation.TranslationService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
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

    @Inject private TranslationService translationService;

    @PostMapping(value = "/create_permission_schema")
    public ResponseEntity<Map<String, Object>> createFLPSchema(@Valid @RequestBody List<ModelDTO> modelDTO)  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.createPermissionSchema(modelDTO));

    }

    @GetMapping(value = "/get_permission_schema")
    public ResponseEntity<Map<String, Object>> getFLPSchema()  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionSchema());
    }

    @GetMapping(value = "/access_group_permissions")
    public ResponseEntity<Map<String, Object>> getAccessGroupPermissions(@RequestParam Set<Long> accessGroupIds )  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionSchema(accessGroupIds,null));
    }

    @GetMapping(value = "/action_permissions")
    public ResponseEntity<Map<String, Object>> getPermissionActions(@RequestParam Long accessGroupId )  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionActions(accessGroupId,null,null));
    }

    @GetMapping(value = "/action_permissions_schema")
    public ResponseEntity<Map<String, Object>> getPermissionActionsSchema()  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionActionsSchema());
    }

    @PutMapping(value = "/update_permission")
    public ResponseEntity<Map<String, Object>> createFieldPermissions(@Valid @RequestBody PermissionDTO permissionDTO,@RequestParam boolean updateOrganisationCategories)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.createPermissions(UserContext.getUserDetails().getLastSelectedOrganizationId(),permissionDTO,updateOrganisationCategories));
    }

    @PutMapping(value = "/update_action_permission")
    public ResponseEntity<Map<String, Object>> updateActionPermissions(@RequestBody CustomPermissionDTO customPermissionDTO)  {
        permissionService.setActionPermissions(customPermissionDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @GetMapping(value = "/get_default_data_of_permission")
    public ResponseEntity<Map<String, Object>> getDefaultDataOfPermission(@RequestParam Long referenceId, @RequestParam ConfLevel confLevel)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getDefaultDataOfPermission(referenceId,confLevel));

    }

    @PutMapping(value = "/validate_permission")
    public ResponseEntity<Map<String, Object>> validatePermission(@RequestParam Long referenceId, @RequestParam ConfLevel confLevel)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getDefaultDataOfPermission(referenceId,confLevel));

    }

    @PostMapping(value = "/unit/{unitId}/fetch_permissions")
    public ResponseEntity<Map<String, Object>> fetchPermission(@RequestBody Set<String> objects, @PathVariable Long unitId)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.fetchPermissions(objects,unitId, UserContext.getUserDetails().getId()));
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

    @PostMapping(value = UNIT_URL+"/access_group/{accessGroupId}/auth/action_permission")
    public ResponseEntity<Map<String, Object>> assignActionPermissionToModel(@PathVariable Long unitId,@PathVariable Long accessGroupId, @RequestBody CustomPermissionDTO customPermissionDTO) {
        permissionService.assignActionPermission(unitId,accessGroupId,customPermissionDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @GetMapping(value = UNIT_URL+"/access_group/{accessGroupId}/auth/action_permission")
    public ResponseEntity<Map<String, Object>> getActionPermissionByAccessGroup(@RequestParam("staffId") Long staffId,@PathVariable Long unitId, @PathVariable Long accessGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.getPermissionActions(accessGroupId,staffId,unitId));
    }

    @PutMapping(value = "/create_action_permission")
    public ResponseEntity<Map<String, Object>> updateActionPermission(@RequestBody List<KPermissionAction> permissionActions) {
        permissionService.createActions(permissionActions);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @PutMapping(value = "/validate_action_permission")
    public ResponseEntity<Map<String, Object>> validateActionPermission(@RequestBody Map<String, Object> permissionActions) {
        String modelName= (String) permissionActions.get("modelName");
        PermissionAction action= PermissionAction.valueOf((String)permissionActions.get("action"));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, permissionService.validPermissionAction(modelName,action, UserContext.getUserDetails().getLastSelectedOrganizationId()));
    }

    @ApiOperation("update translation data")
    @PutMapping(value ="/model_permission/{id}/language_settings" )
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfModelPermission(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, translationService.updateTranslation(id, translations));
    }

    @ApiOperation("update translation data")
    @PutMapping(value ="/action_permission/{id}/language_settings" )
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfActionPermission(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, translationService.updateTranslation(id, translations));
    }

    @ApiOperation("update translation data")
    @PutMapping(value ="/field_permission/{id}/language_settings" )
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfFieldPermission(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, translationService.updateTranslation(id, translations));
    }

}

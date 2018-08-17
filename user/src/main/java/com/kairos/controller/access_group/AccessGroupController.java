package com.kairos.controller.access_group;

import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.user.access_permission.AccessGroupPermissionDTO;
import com.kairos.user.access_permission.AccessPermissionDTO;
import com.kairos.user.country.agreement.cta.cta_response.AccessGroupDTO;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.user.access_group.CountryAccessGroupDTO;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;


/**
 * Created by prabjot on 7/11/16.
 */
@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(value = API_ORGANIZATION_URL)
public class AccessGroupController {

    @Inject
    AccessGroupService accessGroupService;


    @RequestMapping(value = UNIT_URL+"/access_group", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createAccessGroup(@PathVariable long unitId, @RequestBody AccessGroup accessGroup) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, accessGroupService.createAccessGroup(unitId, accessGroup));
    }

    @RequestMapping(value = UNIT_URL+"/access_group/{accessGroupId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateAccessGroup(@PathVariable long unitId, @PathVariable long accessGroupId, @Valid @RequestBody AccessGroupDTO accessGroupDTO) {
        AccessGroup updatedObject = accessGroupService.updateAccessGroup(accessGroupId, unitId, accessGroupDTO);
        if (updatedObject == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
        }
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, updatedObject);
    }

    @RequestMapping(value = UNIT_URL+"/access_group/{accessGroupId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteAccessGroup(@PathVariable long accessGroupId) {
        boolean isObjectDeleted = accessGroupService.deleteAccessGroup(accessGroupId);
        if (isObjectDeleted) {
            return ResponseHandler.generateResponse(HttpStatus.CREATED, true, isObjectDeleted);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
    }


    @RequestMapping(value = UNIT_URL+"/access_group", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAccessGroups(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getAccessGroupsForUnit(unitId));
    }

    @RequestMapping(value = UNIT_URL+"/staff/{staffId}/access_group", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> assignAccessGroupToStaff(@PathVariable long staffId, @RequestBody Map<String, Object> reqData) {

        List<String> accessGroupIds = (List<String>) reqData.get("accessGroupIds");
        boolean isGroupAssigned = accessGroupService.assignAccessGroupToStaff(accessGroupIds, staffId);
        if (isGroupAssigned) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, isGroupAssigned);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, isGroupAssigned);
    }

    @RequestMapping(value = UNIT_URL+"/access_page", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createAccessPage(@RequestBody Map<String, Object> reqData) {
        String name = (String) reqData.get("name");
        boolean isModule = (boolean) reqData.get("isModule");
        List<Map<String, Object>> childPages = (List<Map<String, Object>>) reqData.get("childPages");
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.createAccessPage(name, childPages, isModule));
    }

    @RequestMapping(value = UNIT_URL+"/user/{userId}/organization/{orgId}/access_modules", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAccessModulesForUnits(@PathVariable long userId, @PathVariable long orgId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getAccessModulesForUnits(orgId, userId));
    }

    @RequestMapping(value = UNIT_URL+"/access_permission/unit_employment/{unitEmploymentId}/access_page/{accessPageId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> modifyAccessPagePermission(@PathVariable long unitEmploymentId,
                                                                          @PathVariable long accessPageId, @RequestBody Map<String, Object> map) {
        accessGroupService.modifyAccessPagePermission(unitEmploymentId, accessPageId, (boolean) map.get("read"));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @RequestMapping(value = UNIT_URL+"/access_group/{accessGroupId}/access_page", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAccessPageHierarchy(@PathVariable long accessGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getAccessPageHierarchy(accessGroupId, null));

    }

    @RequestMapping(value = UNIT_URL+"/access_group/{accessGroupId}/access_page", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setAccessPageInGroup(@PathVariable long accessGroupId, @RequestBody AccessGroupPermissionDTO accessGroupPermission) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.setAccessPagePermissions(accessGroupId, accessGroupPermission.getAccessPageIds(), accessGroupPermission.isSelected(), null));
    }

    @RequestMapping(value = UNIT_URL+"/access_group/{accessGroupId}/auth/access_page", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAccessPageByAccessGroup(@RequestParam("unitId") long unitId, @RequestParam("staffId") long staffId,
                                                                          @PathVariable long accessGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getAccessPageByAccessGroup(accessGroupId, unitId,staffId));

    }

    @RequestMapping(value = UNIT_URL+"/access_group/{accessGroupId}/auth/access_page", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> assignPermission(@PathVariable long accessGroupId, @RequestBody AccessPermissionDTO accessPermissionDTO) {
        accessGroupService.assignPermission(accessGroupId,accessPermissionDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);

    }


    @RequestMapping(value = COUNTRY_URL+"/access_group", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createCountryAccessGroup(@PathVariable long countryId,@Valid @RequestBody CountryAccessGroupDTO accessGroupDTO) {
        AccessGroup accessGroup = accessGroupService.createCountryAccessGroup(countryId, accessGroupDTO);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, accessGroup);
    }

    @RequestMapping(value = COUNTRY_URL+"/access_group/{accessGroupId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryAccessGroup(@PathVariable Long countryId, @PathVariable Long accessGroupId,@Valid @RequestBody CountryAccessGroupDTO accessGroupDTO) {
        AccessGroup accessGroup = accessGroupService.updateCountryAccessGroup(countryId, accessGroupId, accessGroupDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroup);
    }


    @RequestMapping(value = COUNTRY_URL+"/access_group/{accessGroupId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCountryAccessGroup(@PathVariable long accessGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.deleteCountryAccessGroup(accessGroupId));
    }

    @ApiOperation("Get organization category with count of Access Groups of country")
    @RequestMapping(value = COUNTRY_URL + "/organization_category" , method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getListOfOrgCategoryWithCountryAccessGroupCount(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessGroupService.getListOfOrgCategoryWithCountryAccessGroupCount(countryId));
    }

    @ApiOperation("Get country Access Groups")
    @RequestMapping(value = COUNTRY_URL + "/access_group/organization_category/{organizationCategory}" , method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryAccessGroups(@PathVariable Long countryId, @PathVariable OrganizationCategory organizationCategory) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessGroupService.getCountryAccessGroups(countryId, organizationCategory));
    }

    @ApiOperation("Get country Access Groups with category")
    @RequestMapping(value = COUNTRY_URL + "/access_group" , method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryAccessGroupsOfAllcategories(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessGroupService.getCountryAccessGroupsOfAllCategories(countryId));
    }

    @RequestMapping(value = COUNTRY_URL+"/access_group/{accessGroupId}/access_page", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAccessPageHierarchyForCountry(@PathVariable long accessGroupId, @PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getAccessPageHierarchy(accessGroupId, countryId));

    }

    @RequestMapping(value = COUNTRY_URL+"/access_group/{accessGroupId}/access_page", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setAccessPageInAccessGroupForCountry(@PathVariable long accessGroupId, @PathVariable Long countryId, @RequestBody AccessGroupPermissionDTO accessGroupPermission) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.setAccessPagePermissions( accessGroupId, accessGroupPermission.getAccessPageIds(), accessGroupPermission.isSelected(), countryId));
    }

    @RequestMapping(value = COUNTRY_URL+"/access_group/{accessGroupId}/access_page/{accessPageId}/permission", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePermissionsForAccessTabsOfAccessGroupOfCountry(@RequestParam(value = "updateChildren") Boolean updateChildren, @PathVariable long accessGroupId, @PathVariable Long accessPageId, @RequestBody AccessPermissionDTO accessPermissionDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.updatePermissionsForAccessTabsOfAccessGroup(accessGroupId, accessPageId, accessPermissionDTO,updateChildren));
    }

    @RequestMapping(value = UNIT_URL+"/access_group/{accessGroupId}/access_page/{accessPageId}/permission", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePermissionsForAccessTabsOfAccessGroupOfOrg(@RequestParam(value = "updateChildren") Boolean updateChildren, @PathVariable long accessGroupId, @PathVariable Long accessPageId, @RequestBody AccessPermissionDTO accessPermissionDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.updatePermissionsForAccessTabsOfAccessGroup(accessGroupId, accessPageId, accessPermissionDTO, updateChildren));
    }

    @RequestMapping(value = UNIT_URL+"/copy_unit_access_group", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copyUnitAccessGroup(@PathVariable long unitId, @Valid @RequestBody AccessGroupDTO accessGroupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.copyUnitAccessGroup(unitId, accessGroupDTO));
    }

    @RequestMapping(value = COUNTRY_URL+"/copy_country_access_group", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copyCountryAccessGroup(@PathVariable long countryId,@Valid @RequestBody CountryAccessGroupDTO countryAccessGroupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.copyCountryAccessGroup(countryId, countryAccessGroupDTO));
    }

    @RequestMapping(value = UNIT_URL+"/current_user/access_role", method = RequestMethod.GET)
    @ApiOperation("To fetch Access Role (Staff/Management) of current logged in user")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> checkIfUserHasAccessByRoleInUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.checkIfUserHasAccessByRoleInUnit(unitId));

    }

    @ApiOperation("Get country Access Groups for hub and organization")
    @RequestMapping(value = COUNTRY_URL + "/access_group/hub_and_organization" , method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryAccessGroupsForOrganizationCreation(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,accessGroupService.getCountryAccessGroupsForOrganizationCreation(countryId));
    }


    @ApiOperation("get staff ids by unit id and accessgroup id")
    @GetMapping(value = UNIT_URL+"/access_group/{accessGroupId}/get_Staff_Ids")
    public ResponseEntity<Map<String, Object>> getStaffIdsByUnitIdAndAccessGroupId(@PathVariable Long unitId,@PathVariable Long accessGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getStaffIdsByUnitIdAndAccessGroupId(unitId,accessGroupId));
    }

}

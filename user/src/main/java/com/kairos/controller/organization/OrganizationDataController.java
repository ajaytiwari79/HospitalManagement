package com.kairos.controller.organization;

import com.kairos.service.organization.CompanyCreationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.user.organization.AddressDTO;
import com.kairos.user.organization.OrganizationBasicDTO;
import com.kairos.user.organization.UnitManagerDTO;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;

/**
 * Created by vipul on 27/9/17.
 */
@RestController

@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class OrganizationDataController {
    @Inject
    OrganizationService organizationService;
    @Inject
    CompanyCreationService companyCreationService;

    /**
     * Create new organization in db and return created organization
     *
     * @param addressDTO
     * @return Organization
     */
    @ApiOperation(value = "update address of parent organization")
    @PutMapping(value = COUNTRY_URL + "/parent_organization/{organizationId}/address")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setAddressInCompany(@Validated @RequestBody AddressDTO addressDTO, @PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.setAddressInCompany(organizationId, addressDTO));
    }

    @ApiOperation(value = "update address of parent organization")
    @PutMapping(value = COUNTRY_URL + "/parent_organization/{organizationId}/on_boarding_done")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> publishOrganization(@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.publishOrganization(organizationId));
    }

    @ApiOperation(value = "get basic details  of parent organization")
    @GetMapping(value = COUNTRY_URL + "/parent_organization/{organizationId}/details")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationDetailsById(@PathVariable  Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.getOrganizationDetailsById(organizationId));
    }

    @ApiOperation(value = "update type and sub type in parent organization")
    @PutMapping(value = COUNTRY_URL + "/parent_organization/{organizationId}/type_details")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setOrganizationTypeAndSubTypeInOrganization(@Validated @RequestBody OrganizationBasicDTO organizationBasicDTO, @PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.setOrganizationTypeAndSubTypeInOrganization(organizationBasicDTO, organizationId));
    }

    @ApiOperation(value = "update unit manager parent organization")
    @PutMapping(value = COUNTRY_URL + "/parent_organization/{organizationId}/manager")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setUserInfoInOrganization(@Validated @RequestBody UnitManagerDTO unitManagerDTO, @PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.setUserInfoInOrganization(organizationId, null, unitManagerDTO));
    }

    @ApiOperation(value = "get address of parent organization")
    @GetMapping(value = COUNTRY_URL + "/parent_organization/{organizationId}/address")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAddressOfCompany(@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, companyCreationService.getAddressOfCompany(organizationId));
    }

    @ApiOperation(value = "get unit manager details of parent organization")
    @GetMapping(value = COUNTRY_URL + "/parent_organization/{organizationId}/manager")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitManagerOfOrganization(@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.getUnitManagerOfOrganization(organizationId));
    }

    @ApiOperation(value = "get organization type and sub type of parent organization")
    @GetMapping(value = COUNTRY_URL + "/parent_organization/{organizationId}/type_details")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationTypeAndSubTypeByUnitId(@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.getOrganizationTypeAndSubTypeByUnitId(organizationId));
    }
}

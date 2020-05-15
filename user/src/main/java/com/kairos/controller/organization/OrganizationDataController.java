package com.kairos.controller.organization;

import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.dto.user.organization.OrganizationBasicDTO;
import com.kairos.dto.user.organization.UnitManagerDTO;
import com.kairos.service.organization.CompanyCreationService;
import com.kairos.service.organization.OrganizationAddressService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created by vipul on 27/9/17.
 */
@RestController

@RequestMapping(API_V1)
@Api(API_V1)
public class OrganizationDataController {
    @Inject
    private OrganizationService organizationService;
    @Inject
    private CompanyCreationService companyCreationService;
    @Inject private OrganizationAddressService organizationAddressService;

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
    public ResponseEntity<Map<String, Object>> publishOrganization(@PathVariable Long countryId,@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.onBoardOrganization(countryId,organizationId,null));
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

    @ApiOperation(value = "Create a New Organization")
    @PostMapping(value = "/parent_organization/{organizationId}/unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrganization(@Validated @RequestBody OrganizationBasicDTO organizationBasicDTO, @PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.addNewUnit(organizationBasicDTO, organizationId));
    }

    @ApiOperation(value = "update  a child Organization")
    @PutMapping(value = "/parent_organization/{organizationId}/unit/{unitId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateUnit(@Validated @RequestBody OrganizationBasicDTO organizationBasicDTO, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                companyCreationService.updateUnit(organizationBasicDTO, unitId));
    }


    @ApiOperation(value = "Update Parent Organization")
    @RequestMapping(value = COUNTRY_URL + "/parent_organization/{parentOrganizationId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateParentOrganization(@PathVariable long countryId, @PathVariable long parentOrganizationId, @Valid @RequestBody OrganizationBasicDTO organizationBasicDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, companyCreationService.updateParentOrganization(organizationBasicDTO, parentOrganizationId));
    }

    @ApiOperation(value = "get organization Location and reason code by Ids for shift details")
    @GetMapping(value = UNIT_URL + UNIT_LOCATION_AND_REASON_CODE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAddressAndReasonCodeOfOrganization(@PathVariable Long unitId, @RequestParam("absenceReasonCodeIds") Set<Long> absenceReasonCodeIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,organizationAddressService.getAddressAndReasonCodeOfOrganization(absenceReasonCodeIds,unitId ));
    }


}

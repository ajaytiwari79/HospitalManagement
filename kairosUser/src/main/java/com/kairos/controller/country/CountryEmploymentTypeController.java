package com.kairos.controller.country;

import com.kairos.client.dto.organization.OrganizationEmploymentTypeDTO;
import com.kairos.persistence.model.user.country.dto.EmploymentTypeDTO;
import com.kairos.service.country.EmploymentTypeService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created by prerna on 2/11/17.
 */
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
@RestController
public class CountryEmploymentTypeController {

    @Inject
    private EmploymentTypeService employmentTypeService;

    @RequestMapping(value = COUNTRY_URL + "/employment_type", method = RequestMethod.POST)
    @ApiOperation("Add employment type in country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addEmploymentType(@PathVariable long countryId, @Valid @RequestBody EmploymentTypeDTO employmentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, employmentTypeService.addEmploymentType(countryId, employmentTypeDTO));
    }

    @RequestMapping(value = COUNTRY_URL + "/employment_type/{employmentTypeId}", method = RequestMethod.PUT)
    @ApiOperation("Update employment type in country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateEmploymentType(@PathVariable long countryId, @PathVariable long employmentTypeId, @RequestBody EmploymentTypeDTO employmentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.updateEmploymentType(countryId, employmentTypeId, employmentTypeDTO));
    }

    @RequestMapping(value = COUNTRY_URL + "/employment_type/{employmentTypeId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete employment type of country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteEmploymentType(@PathVariable long countryId, @PathVariable long employmentTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.deleteEmploymentType(countryId, employmentTypeId));
    }

    @RequestMapping(value = COUNTRY_URL + "/employment_type", method = RequestMethod.GET)
    @ApiOperation("get employment type of country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEmploymentTypeList (@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getEmploymentTypeList(countryId, false));
    }

    @RequestMapping(value = UNIT_URL+"/employment_type/{employmentTypeId}", method = RequestMethod.PUT)
    @ApiOperation("Add relationship for organization and employment type for settings")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addEmploymentTypeSettingsForOrganization(@PathVariable long unitId, @PathVariable long employmentTypeId, @Valid @RequestBody OrganizationEmploymentTypeDTO orgEmploymentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.setEmploymentTypeSettingsOfOrganization(unitId, employmentTypeId, orgEmploymentTypeDTO));
    }

    @RequestMapping(value = UNIT_URL+"/employment_type", method = RequestMethod.GET)
    @ApiOperation("Add relationship for organization and employment type for settings")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEmploymentTypeSettinggsForOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getEmploymentTypeSettingsOfOrganization(unitId));
    }
    /*
    * By Vipul
    * API to get expertise level region employment Type organizationType for activity type MASTER DATA
    */
    @RequestMapping(value = COUNTRY_URL + "/employment_type_with_organizationType", method = RequestMethod.GET)
    @ApiOperation("get  expertise level region employment Type organizationType  of country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationMappingDetails (@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getOrganizationMappingDetails(countryId));
    }
}

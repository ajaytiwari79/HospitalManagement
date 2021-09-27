package com.kairos.controller.country;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.user.organization.OrganizationEmploymentTypeDTO;
import com.kairos.persistence.model.country.default_data.EmploymentTypeDTO;
import com.kairos.service.country.EmploymentTypeService;
import com.kairos.service.translation.TranslationService;
import com.kairos.utils.response.ResponseHandler;
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
@RequestMapping(API_V1)
@Api(API_V1)
@RestController
public class CountryEmploymentTypeController {

    @Inject
    private EmploymentTypeService employmentTypeService;
    @Inject private TranslationService translationService;

    @RequestMapping(value = COUNTRY_URL + "/employment_type", method = RequestMethod.POST)
    @ApiOperation("Add employment type in country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addEmploymentType(@PathVariable long countryId, @Valid @RequestBody EmploymentTypeDTO employmentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, employmentTypeService.addEmploymentType(countryId, employmentTypeDTO));
    }

    @RequestMapping(value = COUNTRY_URL + "/employment_type/{employmentTypeId}", method = RequestMethod.PUT)
    @ApiOperation("Update employment type in country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateEmploymentType(@PathVariable long countryId, @PathVariable long employmentTypeId, @Valid @RequestBody EmploymentTypeDTO employmentTypeDTO) {
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
    public ResponseEntity<Map<String, Object>> getOrganizationMappingDetails (@PathVariable Long countryId ) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getOrganizationMappingDetails(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/employment_type_and_expertise", method = RequestMethod.GET)
    @ApiOperation("get employment type of country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseAndEmployment (@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getExpertiseAndEmployment(countryId, false));
    }

    @RequestMapping(value = UNIT_URL + "/employment_type_and_expertise", method = RequestMethod.GET)
    @ApiOperation("get employment type of country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseAndEmploymentForUnit (@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getExpertiseAndEmploymentForUnit(unitId, false));
    }

    @RequestMapping(value = COUNTRY_URL + "/day_types_and_employment_types", method = RequestMethod.GET)
    @ApiOperation("get employment type and dayTypes of country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayTypesAndEmploymentTypes (@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getDayTypesAndEmploymentTypes(countryId,false));
    }

    @RequestMapping(value = UNIT_URL + "/day_types_and_employment_types", method = RequestMethod.GET)
    @ApiOperation("get employment type and dayTypes of country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayTypesAndEmploymentTypesAtUnit (@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getDayTypesAndEmploymentTypesAtUnit(unitId,false));
    }

    @RequestMapping(value =COUNTRY_URL+"/staff_by_kpi_filter", method = RequestMethod.POST)
    @ApiOperation("get staff by employment type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffByEmploymentType (@RequestBody StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getStaffByKpiFilter(staffEmploymentTypeDTO));
    }

    @RequestMapping(value =COUNTRY_URL+"/kpi_default_data", method = RequestMethod.POST)
    @ApiOperation("get staff by employment type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getKpiDefaultData(@RequestBody StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getKpiDefaultData(staffEmploymentTypeDTO));
    }

    @RequestMapping(value =UNIT_URL+"/kpi_filter_default_data", method = RequestMethod.GET)
    @ApiOperation("get staff by employment type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getKpifilterDefaultData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getKpiFilterDefaultData(unitId));
    }

    @PostMapping(value =COUNTRY_URL+"/kpi_all_default_data")
    @ApiOperation("get all default data")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getKpiAllDefaultData(@RequestBody StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getKpiAllDefaultData(staffEmploymentTypeDTO));
    }
    @RequestMapping(value = UNIT_URL+"/employment_types", method = RequestMethod.GET)
    @ApiOperation("Get all employment type of the unit")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEmploymentTypes(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentTypeService.getEmploymentTypes(unitId));
    }

    @RequestMapping(value = COUNTRY_URL+"/employment_type/{id}/language_settings", method = RequestMethod.PUT)
    @ApiOperation("update translation data")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfEmploymentTypes(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, translationService.updateTranslation(id,translations));
    }
    @RequestMapping(value = UNIT_URL+"/employment_type/{id}/language_settings", method = RequestMethod.PUT)
    @ApiOperation("update translation data")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTranslationOfEmploymentTypesOfOrganization(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, translationService.updateTranslation(id,translations));
    }

}

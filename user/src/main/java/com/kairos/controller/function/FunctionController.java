package com.kairos.controller.function;

import com.kairos.annotations.KPermissionActions;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.dto.user.TranslationDTO;
import com.kairos.enums.kpermissions.PermissionAction;
import com.kairos.service.country.FunctionService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.*;


@RestController
@RequestMapping()
public class FunctionController {
    @Inject
    private FunctionService functionService;

    @ApiOperation(value = "")
    @PostMapping(API_ORGANIZATION_UNIT_URL + "/appliedFunctionsByEmploymentIds")
    public ResponseEntity<Map<String, Object>> getEmploymentIdWithFunctionIdShiftDateMap(@RequestBody Set<Long> employmentIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                functionService.getEmploymentIdWithFunctionIdShiftDateMap(employmentIds));
    }

    @ApiOperation(value = "")
    @PostMapping(API_ORGANIZATION_UNIT_URL + "/updateFunctionOnPhaseRestoration")
    public ResponseEntity<Map<String, Object>> updateEmploymentFunctionRelationShipDates(@RequestBody Map<Long, Map<LocalDate, Long>> employmentIdWithShiftDateFunctionIdMap) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                functionService.updateEmploymentFunctionRelationShipDates(employmentIdWithShiftDateFunctionIdMap));
    }
    //Functions

    @ApiOperation(value = "Add function by countryId")
    @RequestMapping(value = API_V1 + COUNTRY_URL + "/function", method = RequestMethod.POST)
    @KPermissionActions(modelName = "Function",action = PermissionAction.ADD)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addFunction(@PathVariable long countryId, @Validated @RequestBody FunctionDTO functionDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.createFunction(countryId, functionDTO));
    }

    @ApiOperation(value = "Get functions by countryId")
    @RequestMapping(value = API_V1 + COUNTRY_URL + "/functions", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getFunctions(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.getFunctionsByCountry(countryId));

    }

    @ApiOperation(value = "Update functions")
    @RequestMapping(value = API_V1 + COUNTRY_URL + "/function/{functionId}", method = RequestMethod.PUT)
    @KPermissionActions(modelName = "Function",action = PermissionAction.EDIT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateFunction(@PathVariable long countryId, @Validated @RequestBody FunctionDTO functionDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.updateFunction(countryId, functionDTO));
    }

    @ApiOperation(value = "Delete function by functionId")
    @RequestMapping(value = API_V1 + COUNTRY_URL + "/function/{functionId}", method = RequestMethod.DELETE)
    @KPermissionActions(modelName = "Function",action = PermissionAction.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteFunction(@PathVariable long functionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.deleteFunction(functionId));
    }

    @ApiOperation(value = "Get functions by expertise id")
    @RequestMapping(value = API_V1 + "/function", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getFunctionsByExpertiseId(@RequestParam(value = "expertise") Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.getFunctionsByExpertiseId(expertiseId));

    }

    @ApiOperation(value = "Get functions by countryId")
    @RequestMapping(value = API_V1 + UNIT_URL + "/functions", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getFunctionsAtUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.getFunctionsAtUnit(unitId));

    }

    @GetMapping(value = API_V1 + UNIT_URL + "/employment/functions")
    @ApiOperation("find functions")
    public ResponseEntity<Map<String, Object>> findAppliedFunctionsAtEmployment(@PathVariable Long unitId, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.findAppliedFunctionsAtEmployment(unitId, startDate, endDate));
    }

    @ApiOperation(value = "get all date by function ids")
    @PostMapping(API_V1 + UNIT_URL + "/get_functions_date")
    public ResponseEntity<Map<String, Object>> getAllDateByfunctionIds(@PathVariable Long unitId, @RequestBody List<Long> functionIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.getAllDateByFunctionIds(unitId, functionIds));
    }

    @ApiOperation(value = "add translated data")
    @PostMapping(API_V1 + UNIT_URL + "/function/{functionId}/update_translation")
    public ResponseEntity<Map<String, Object>> updateTranslation(@PathVariable Long functionId, @RequestBody TranslationDTO translationData) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.updateTranslation(functionId, translationData));
    }

    @ApiOperation(value = "get translated data")
    @GetMapping(API_V1 + UNIT_URL + "/function/{functionId}/translation")
    public ResponseEntity<Map<String, Object>> getTranslatedData(@PathVariable Long functionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.getTranslatedData(functionId));
    }

    @ApiOperation(value = "add translated data")
    @PutMapping(API_V1 + COUNTRY_URL + "/function/{id}/language_settings")
    public ResponseEntity<Map<String, Object>> updateTranslationOfCountryFunctions(@PathVariable Long id, @RequestBody Map<String,TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.updateTranslationOfCountryFunctions(id, translations));
    }


}

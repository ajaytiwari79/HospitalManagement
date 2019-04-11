package com.kairos.controller.function;

import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.service.country.FunctionService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.*;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

@RestController
@RequestMapping()
public class FunctionController {
    @Inject
    private FunctionService functionService;

    //===============================================================
    @ApiOperation(value = "")
    @PostMapping(API_ORGANIZATION_UNIT_URL+"/appliedFunctionsByEmploymentIds")
    public ResponseEntity<Map<String, Object>> getEmploymentIdWithFunctionIdShiftDateMap(@RequestBody Set<Long> employmentIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                functionService.getEmploymentIdWithFunctionIdShiftDateMap(employmentIds));
    }

    @ApiOperation(value = "")
    @PostMapping(API_ORGANIZATION_UNIT_URL+"/updateFunctionOnPhaseRestoration")
    public ResponseEntity<Map<String, Object>> updateEmploymentFunctionRelationShipDates(@RequestBody Map<Long, Map<LocalDate, Long>> employmentIdWithShiftDateFunctionIdMap) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                functionService.updateEmploymentFunctionRelationShipDates(employmentIdWithShiftDateFunctionIdMap));
    }
    //Functions

    @ApiOperation(value = "Add function by countryId")
    @RequestMapping(value = API_V1 +COUNTRY_URL + "/function", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addFunction(@PathVariable long countryId, @Validated @RequestBody FunctionDTO functionDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.createFunction(countryId, functionDTO));
    }

    @ApiOperation(value = "Get functions by countryId")
    @RequestMapping(value = API_V1 +COUNTRY_URL + "/functions", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getFunctions(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.getFunctionsByCountry(countryId));

    }

    @ApiOperation(value = "Update functions")
    @RequestMapping(value = API_V1 +COUNTRY_URL + "/function/{functionId}", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateFunction(@PathVariable long countryId, @Validated @RequestBody FunctionDTO functionDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.updateFunction(countryId, functionDTO));
    }

    @ApiOperation(value = "Delete function by functionId")
    @RequestMapping(value = API_V1 +COUNTRY_URL + "/function/{functionId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteFunction(@PathVariable long functionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.deleteFunction(functionId));
    }
    @ApiOperation(value = "Get functions by expertise id")
    @RequestMapping(value =  API_V1 +"/function", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getFunctionsByExpertiseId(@RequestParam(value = "expertise") Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.getFunctionsByExpertiseId(expertiseId));

    }

    @ApiOperation(value = "Get functions by countryId")
    @RequestMapping(value = API_V1+UNIT_URL + "/functions", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getFunctionsAtUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.getFunctionsAtUnit(unitId));

    }

    @GetMapping(value =API_V1+UNIT_URL +"/unit_position/functions")
    @ApiOperation("find functions")
    public ResponseEntity<Map<String, Object>> findAppliedFunctionsAtEmployment(@PathVariable Long unitId,@RequestParam("startDate") String startDate,@RequestParam("endDate") String endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.findAppliedFunctionsAtEmployment(unitId,startDate,endDate));
    }

}

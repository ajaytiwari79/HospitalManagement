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
    @PostMapping(API_ORGANIZATION_UNIT_URL+"/appliedFunctionsByUnitPositionIds")
    public ResponseEntity<Map<String, Object>> getUnitPositionIdWithFunctionIdShiftDateMap(@RequestBody Set<Long> unitPositionIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                functionService.getUnitPositionIdWithFunctionIdShiftDateMap(unitPositionIds));
    }

    @ApiOperation(value = "")
    @PostMapping(API_ORGANIZATION_UNIT_URL+"/updateFunctionOnPhaseRestoration")
    public ResponseEntity<Map<String, Object>> updateUnitPositionFunctionRelationShipDates(@RequestBody Map<Long, Map<LocalDate, Long>> unitPositionIdWithShiftDateFunctionIdMap) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                functionService.updateUnitPositionFunctionRelationShipDates(unitPositionIdWithShiftDateFunctionIdMap));
    }
    //Functions

    @ApiOperation(value = "Add function by countryId")
    @RequestMapping(value = API_ORGANIZATION_URL+COUNTRY_URL + "/function", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addFunction(@PathVariable long countryId, @Validated @RequestBody FunctionDTO functionDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.createFunction(countryId, functionDTO));
    }

    @ApiOperation(value = "Get functions by countryId")
    @RequestMapping(value = API_ORGANIZATION_URL+COUNTRY_URL + "/functions", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getFunctions(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.getFunctionsByCountry(countryId));

    }

    @ApiOperation(value = "Update functions")
    @RequestMapping(value = API_ORGANIZATION_URL+COUNTRY_URL + "/function/{functionId}", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateFunction(@PathVariable long countryId, @Validated @RequestBody FunctionDTO functionDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.updateFunction(countryId, functionDTO));
    }

    @ApiOperation(value = "Delete function by functionId")
    @RequestMapping(value = API_ORGANIZATION_URL+COUNTRY_URL + "/function/{functionId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteFunction(@PathVariable long functionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, functionService.deleteFunction(functionId));
    }
    @ApiOperation(value = "Get functions by expertise id")
    @RequestMapping(value =  API_ORGANIZATION_URL+"/function", method = RequestMethod.GET)
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

}

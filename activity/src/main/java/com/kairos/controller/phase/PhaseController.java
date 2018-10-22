package com.kairos.controller.phase;

import com.kairos.service.phase.PhaseService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.dto.activity.phase.PhaseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by vipul on 25/9/17.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL+"/phase")
@Api(API_ORGANIZATION_UNIT_URL+"/phase")
public class PhaseController {

    @Inject
    private PhaseService phaseService;


   /* @ApiOperation(value = "Create Phases in Organization")
    @PostMapping(value="")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createPhase(@PathVariable Long unitId, @RequestBody @Valid PhaseDTO phaseDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, phaseService.createPhasesByUnitId(unitId, phaseDTO));
    }*/

    @ApiOperation(value = "Create all 4 default phases in Phases in Organization")
    @PostMapping(value="/default")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createDefaultPhase(@PathVariable Long unitId, @RequestParam("countryId") Long countryId) {
        phaseService.createDefaultPhase(unitId, countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @ApiOperation(value = "Get All phases by unit Id")
    @GetMapping(value="")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCategorisedPhasesByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, phaseService.getCategorisedPhasesByUnit(unitId));
    }

    @ApiOperation(value = "update phases by unit Id and PhaseId")
    @PutMapping(value = "/{phaseId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePhase(@PathVariable BigInteger phaseId, @PathVariable Long unitId, @RequestBody @Valid PhaseDTO phaseDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, phaseService.updatePhase(phaseId, unitId,phaseDTO));
    }

    @ApiOperation(value = "Remove phase")
    @DeleteMapping(value = "/{phaseId}")
    public ResponseEntity<Map<String, Object>> deletePhase(@PathVariable BigInteger phaseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, phaseService.removePhase(phaseId));
    }

    @ApiOperation(value = "get ongoing phase based on a date ")
    @GetMapping(value = "/byDate")
    public ResponseEntity<Map<String, Object>> getUnitPhaseByDate(@PathVariable Long unitId,@RequestParam(value = "date",required = false)Date date) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, phaseService.getUnitPhaseByDate(unitId,date));
    }

    @ApiOperation(value = "get Unit Default Phases ")
    @GetMapping(value = "/all")
    public ResponseEntity<Map<String, Object>> getDefaultPhasesByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, phaseService.getDefaultPhasesByUnit(unitId));
    }
}


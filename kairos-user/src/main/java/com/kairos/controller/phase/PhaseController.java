package com.kairos.controller.phase;

import com.kairos.persistence.model.user.phase.PhaseDTO;
import com.kairos.service.phase.PhaseService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by pawanmandhan on 29/8/17.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL+"/phase")
@Api(API_ORGANIZATION_UNIT_URL)
public class PhaseController {

    @Inject
    private PhaseService phaseService;


    @ApiOperation(value = "Create Phases in Organization")
    @PostMapping(value="")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createPhase(@PathVariable Long unitId,@RequestBody @Valid  PhaseDTO phaseDTO) {
        phaseService.createPhasesByUnitId(unitId, phaseDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }


    @ApiOperation(value = "Get ALl phases by unit Id")
    @GetMapping(value="")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPhasesByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, phaseService.getPhasesByUnit(unitId));
    }

    @ApiOperation(value = "update  phases by unit Id and PhaseId")
    @PutMapping(value = "/{phaseId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePhase(@PathVariable Long unitId, @PathVariable Long phaseId, @RequestBody PhaseDTO phaseDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, phaseService.updatePhase(unitId,phaseId,phaseDTO));
    }

    @ApiOperation(value = "Remove phase")
    @DeleteMapping(value = "/{phaseId}")
    public ResponseEntity<Map<String, Object>> deletePhase(@PathVariable Long phaseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, phaseService.removePhase(phaseId));
    }


}

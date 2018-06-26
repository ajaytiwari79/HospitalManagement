package com.kairos.activity.controller.shift;

import com.kairos.activity.service.shift.ShiftTemplateService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.shift.IndividualShiftTemplateDTO;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class ShiftTemplateController {
    @Inject private ShiftTemplateService shiftTemplateService;

    @ApiOperation("Create Shift template for a staff")
    @PostMapping(value = "/shift_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createShiftTemplate(@PathVariable Long unitId, @RequestBody @Valid ShiftTemplateDTO shiftTemplateDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.createShiftTemplate(unitId, shiftTemplateDTO));
    }

    @ApiOperation("Get Shift Templates by UnitId")
    @GetMapping(value = "/shift_templates")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShiftTemplatesByUnitId( @PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.getAllShiftTemplates(unitId));
    }

    @ApiOperation("update  Shift Template")
    @PutMapping(value = "/shift_template/{shiftTemplateId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShiftTemplate(@PathVariable BigInteger shiftTemplateId,  @RequestBody @Valid ShiftTemplateDTO shiftTemplateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.updateShiftTemplate(shiftTemplateId, shiftTemplateDTO));
    }


    @ApiOperation("delete a Shift Template")
    @DeleteMapping(value = "/shift_template/{shiftTemplateId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable BigInteger shiftTemplateId) {
        shiftTemplateService.deleteShiftTemplate(shiftTemplateId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation("add Individual Shift Template")
    @PostMapping(value = "shift_template/{shiftTemplateId}/individual_shift_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addIndividualShiftTemplate(@PathVariable BigInteger shiftTemplateId,  @RequestBody @Valid IndividualShiftTemplateDTO individualShiftTemplateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.addIndividualShiftTemplate(shiftTemplateId, individualShiftTemplateDTO));
    }

    @ApiOperation("update Individual Shift Template")
    @PutMapping(value = "/individual_shift_template/{IndividualShiftTemplateId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateIndividualShift(@PathVariable BigInteger individualShiftTemplateId,  @RequestBody @Valid IndividualShiftTemplateDTO individualShiftTemplateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.updateIndividualShiftTemplate(individualShiftTemplateId, individualShiftTemplateDTO));
    }


    @ApiOperation("delete Individual Shift Template")
    @PostMapping(value = "shift_template/{shiftTemplateId}/individual_shift_template/{IndividualShiftTemplateId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteIndividualShiftTemplate(@PathVariable BigInteger shiftTemplateId,  @PathVariable BigInteger individualShiftTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.deleteIndividualShiftTemplate(shiftTemplateId, individualShiftTemplateId));
    }


}

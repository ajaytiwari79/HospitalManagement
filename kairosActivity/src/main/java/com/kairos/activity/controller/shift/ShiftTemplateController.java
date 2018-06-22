package com.kairos.activity.controller.shift;

import com.kairos.activity.service.shift.ShiftDayTemplateService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.shift.ShiftDayTemplateDTO;
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
    @Inject private ShiftDayTemplateService shiftDayTemplateService;

    @ApiOperation("Create Shift template for a staff")
    @PostMapping(value = "/shift_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createShiftTemplate(@PathVariable Long unitId, @RequestBody @Valid ShiftDayTemplateDTO shiftDayTemplateDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftDayTemplateService.createShiftTemplate(unitId, shiftDayTemplateDTO));
    }

    @ApiOperation("Get Shift Templates by UnitId")
    @GetMapping(value = "/shift_templates")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShiftTemplatesByUnitId( @PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftDayTemplateService.getAllShiftTemplates(unitId));
    }

    @ApiOperation("update a Shift Template")
    @PutMapping(value = "/shift_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShift(@PathVariable Long unitId,  @RequestBody @Valid ShiftDayTemplateDTO shiftDayTemplateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftDayTemplateService.updateShiftTemplate(unitId, shiftDayTemplateDTO));
    }

    @ApiOperation("delete a Shift Template")
    @DeleteMapping(value = "/shift_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable Long unitId,@PathVariable BigInteger shiftId) {
        shiftDayTemplateService.deleteShiftTemplate(unitId,shiftId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation("Get All Shift Templates by staffId")
    @GetMapping(value = "/shift_templates/staff/{staffId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllShiftTemplatesByStaffId( @PathVariable Long unitId,@RequestParam Long staffId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftDayTemplateService.getAllShiftTemplatesByStaffId(unitId,staffId));
    }


}

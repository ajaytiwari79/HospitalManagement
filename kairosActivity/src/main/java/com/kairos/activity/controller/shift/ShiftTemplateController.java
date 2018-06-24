package com.kairos.activity.controller.shift;

import com.kairos.activity.service.shift.ShiftTemplateService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.shift.ShiftCreationPojoData;
import com.kairos.response.dto.web.shift.ShiftDayTemplateDTO;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.time.LocalDate;
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

    @ApiOperation("update a Shift Template")
    @PutMapping(value = "/shift_template/{shiftDayTemplateId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShift(@PathVariable BigInteger shiftDayTemplateId,  @RequestBody @Valid ShiftDayTemplateDTO shiftDayTemplateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.updateShiftDayTemplate(shiftDayTemplateId, shiftDayTemplateDTO));
    }

    @ApiOperation("delete a Shift Template")
    @DeleteMapping(value = "/shift_template/{shiftTemplateId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable BigInteger shiftTemplateId) {
        shiftTemplateService.deleteShiftTemplate(shiftTemplateId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation("create shift using template")
    @PostMapping(value = "shift/shift_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createShiftUsingTemplate(@PathVariable Long unitId, @RequestBody ShiftCreationPojoData shiftCreationPojoData){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.createShiftUsingTemplate(unitId,shiftCreationPojoData));
    }


}

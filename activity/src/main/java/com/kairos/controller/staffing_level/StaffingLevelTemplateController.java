package com.kairos.controller.staffing_level;

import com.kairos.activity.staffing_level.StaffingLevelTemplateDTO;
import com.kairos.service.staffing_level.StaffingLevelTemplateService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/staffing_level_template")
@Api(value = API_ORGANIZATION_UNIT_URL + "/staffing_level_template")
public class StaffingLevelTemplateController {

    private Logger logger= LoggerFactory.getLogger(StaffingLevelTemplateController.class);
    @Autowired
    private StaffingLevelTemplateService staffingLevelTemplateService;

    @PostMapping(value = "/")
    @ApiOperation("Create staffing level template ")
    public ResponseEntity<Map<String, Object>> addStaffingLevelTemplate(
            @RequestBody @Valid StaffingLevelTemplateDTO staffingLevelTemplateDTO) {
        StaffingLevelTemplateDTO levelTemplateDTO=staffingLevelTemplateService.createStaffingLevelTemplate(staffingLevelTemplateDTO);
        if(!levelTemplateDTO.getErrors().isEmpty()){
            return ResponseHandler.invalidResponse(HttpStatus.PRECONDITION_REQUIRED, false, levelTemplateDTO.getErrors());
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, levelTemplateDTO);
    }


    @PutMapping(value = "/{id}")
    @ApiOperation("update staffing_level template ")
    public ResponseEntity<Map<String, Object>> updateStaffingLevel(
            @RequestBody @Valid StaffingLevelTemplateDTO staffingLevelTemplateDTO, @PathVariable BigInteger id) {
         return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelTemplateService.updateStaffingTemplate(staffingLevelTemplateDTO,id));
    }

    @GetMapping(value = "/")
    @ApiOperation("update staffing_level template ")
    public ResponseEntity<Map<String, Object>> getValidStaffingLevelTemplates(@PathVariable Long unitId,
      @RequestParam(value = "selectedDate",required = false)@DateTimeFormat(pattern="yyyy-MM-dd")Date selectedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelTemplateService.getStaffingLevelTemplates(unitId,selectedDate));
    }

    @DeleteMapping(value = "/{staffingLevelTemplateId}")
    @ApiOperation("delete staffing_level template ")
    public ResponseEntity<Map<String, Object>> deleteStaffingLevelTemplate(@PathVariable BigInteger staffingLevelTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelTemplateService.deleteStaffingLevelTemplate(staffingLevelTemplateId));

    }
}

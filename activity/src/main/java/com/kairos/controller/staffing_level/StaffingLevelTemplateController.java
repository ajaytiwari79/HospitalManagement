package com.kairos.controller.staffing_level;

import com.kairos.dto.activity.staffing_level.StaffingLevelTemplateDTO;
import com.kairos.service.staffing_level.StaffingLevelTemplateService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL + "/staffing_level_template")
@Api(value = API_UNIT_URL + "/staffing_level_template")
public class StaffingLevelTemplateController {

    private static final Logger LOGGER= LoggerFactory.getLogger(StaffingLevelTemplateController.class);
    @Inject
    private StaffingLevelTemplateService staffingLevelTemplateService;

    @PostMapping(value = "/")
    @ApiOperation("Create staffing level template ")
    public ResponseEntity<Map<String, Object>> addStaffingLevelTemplate(
           @PathVariable Long unitId, @RequestBody @Valid StaffingLevelTemplateDTO staffingLevelTemplateDTO) {
         staffingLevelTemplateDTO=staffingLevelTemplateService.createStaffingLevelTemplate(unitId,staffingLevelTemplateDTO);
        if(!staffingLevelTemplateDTO.getErrors().isEmpty()){
            return ResponseHandler.invalidResponse(HttpStatus.PRECONDITION_REQUIRED, false, staffingLevelTemplateDTO.getErrors());
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffingLevelTemplateDTO);
    }


    @PutMapping(value = "/{id}")
    @ApiOperation("update staffing_level template ")
    public ResponseEntity<Map<String, Object>> updateStaffingLevel(
            @RequestBody @Valid StaffingLevelTemplateDTO staffingLevelTemplateDTO, @PathVariable BigInteger id) {
        StaffingLevelTemplateDTO response=staffingLevelTemplateService.updateStaffingLevelTemplte(staffingLevelTemplateDTO,id);
        if(!response.getErrors().isEmpty()){
            return ResponseHandler.invalidResponse(HttpStatus.PRECONDITION_REQUIRED, false, response.getErrors());
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @GetMapping(value = "/")
    @ApiOperation("get staffing_level template ")
    public ResponseEntity<Map<String, Object>> getValidStaffingLevelTemplates(@PathVariable Long unitId,
      @RequestParam(value = "selectedDate",required = false)@DateTimeFormat(pattern="yyyy-MM-dd")Date selectedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelTemplateService.getStaffingLevelTemplates(unitId,selectedDate));
    }

    @DeleteMapping(value = "/{staffingLevelTemplateId}")
    @ApiOperation("delete staffing_level template ")
    public ResponseEntity<Map<String, Object>> deleteStaffingLevelTemplate(@PathVariable BigInteger staffingLevelTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.NO_CONTENT, true,
                staffingLevelTemplateService.deleteStaffingLevelTemplate(staffingLevelTemplateId));

    }
}

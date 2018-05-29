package com.kairos.activity.controller.activity;

import com.kairos.activity.service.activity.PlannedTimeTypeService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.wta.PresenceTypeDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.activity.constants.ApiConstants.COUNTRY_URL;

/*
 * @author: Mohit Shakya
 * @usage: planned time type operations controller
 */
@RestController
@RequestMapping(API_ORGANIZATION_URL+COUNTRY_URL+"/plannedTimeType")
public class PlannedTimeTypeController {

    @Inject
    private PlannedTimeTypeService plannedTimeTypeService;

    @ApiOperation(value = "Add PlannedTimeType by countryId")
    @PostMapping
    public ResponseEntity<Map<String, Object>> addPlannedTimeType(@PathVariable Long countryId, @Validated @RequestBody PresenceTypeDTO presenceTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, plannedTimeTypeService.addPresenceType(presenceTypeDTO, countryId));
    }

    @ApiOperation(value = "Get all PlannedTimeType by countryId")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPlannedTimeTypeByCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannedTimeTypeService.getAllPresenceTypeByCountry(countryId));
    }

    @ApiOperation(value = "delete a PlannedTimeType by Id")
    @DeleteMapping(value="/{plannedTimeTypeId}")
    public ResponseEntity<Map<String, Object>> deletePlannedTimeTypeById(@PathVariable Long plannedTimeTypeId) {
        plannedTimeTypeService.deletePresenceTypeById(plannedTimeTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @ApiOperation(value = "Update PlannedTimeType by Id")
    @PutMapping("/{plannedTimeTypeId}")
    public ResponseEntity<Map<String, Object>> updatePlannedTimeType(@PathVariable Long countryId,
                                                                  @PathVariable Long plannedTimeTypeId,
                                                                  @Validated @RequestBody PresenceTypeDTO presenceTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannedTimeTypeService.updatePresenceType(countryId, plannedTimeTypeId, presenceTypeDTO));
    }
}

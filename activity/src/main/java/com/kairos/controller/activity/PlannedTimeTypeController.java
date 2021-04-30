package com.kairos.controller.activity;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/*
 * @author: Mohit Shakya
 * @usage: planned time type operations controller
 */
@RestController
@RequestMapping(API_V1)
public class PlannedTimeTypeController {

    @Inject
    private PlannedTimeTypeService plannedTimeTypeService;

    @ApiOperation(value = "Add PlannedTimeType by countryId")
    @PostMapping(value = COUNTRY_URL + "/plannedTimeType")
    public ResponseEntity<Map<String, Object>> addPlannedTimeType(@PathVariable Long countryId, @Validated @RequestBody PresenceTypeDTO presenceTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, plannedTimeTypeService.addPresenceType(presenceTypeDTO, countryId));
    }

    @ApiOperation(value = "Get all PlannedTimeType by countryId")
    @GetMapping(value = COUNTRY_URL + "/plannedTimeType")
    public ResponseEntity<Map<String, Object>> getAllPlannedTimeTypeByCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannedTimeTypeService.getAllPresenceTypeByCountry(countryId));
    }

    @ApiOperation(value = "delete a PlannedTimeType by Id")
    @DeleteMapping(value = COUNTRY_URL + "/plannedTimeType/{plannedTimeTypeId}")
    public ResponseEntity<Map<String, Object>> deletePlannedTimeTypeById(@PathVariable BigInteger plannedTimeTypeId) {
        plannedTimeTypeService.deletePresenceTypeById(plannedTimeTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @ApiOperation(value = "Update PlannedTimeType by Id")
    @PutMapping(value = COUNTRY_URL + "/plannedTimeType/{plannedTimeTypeId}")
    public ResponseEntity<Map<String, Object>> updatePlannedTimeType(@PathVariable Long countryId,
                                                                     @PathVariable BigInteger plannedTimeTypeId,
                                                                     @Validated @RequestBody PresenceTypeDTO presenceTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannedTimeTypeService.updatePresenceType(countryId, plannedTimeTypeId, presenceTypeDTO));
    }

    @ApiOperation(value = "Get all PlannedTimeType by countryId")
    @GetMapping(value = UNIT_URL + "/plannedTimeType")
    public ResponseEntity<Map<String, Object>> getAllPresenceTypesByCountry(@RequestParam Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannedTimeTypeService.getAllPresenceTypesByCountry(UserContext.getCountryId()));
    }

    @ApiOperation(value = "update translation of PlannedTimeType by Id")
    @PutMapping(value = COUNTRY_URL + "/plannedTimeType/{plannedTimeTypeId}/language_settings")
    public ResponseEntity<Map<String, Object>> updateTranslationOfPlannedTimeType(@PathVariable BigInteger plannedTimeTypeId, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, plannedTimeTypeService.updateTranslation(plannedTimeTypeId,translations));
    }


}

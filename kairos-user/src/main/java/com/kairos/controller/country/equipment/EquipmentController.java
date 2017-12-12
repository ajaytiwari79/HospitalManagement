package com.kairos.controller.country.equipment;

import com.kairos.response.dto.web.equipment.EquipmentDTO;
import com.kairos.service.country.equipment.EquipmentService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

/**
 * Created by prerna on 12/12/17.
 */
@RestController

@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class EquipmentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    EquipmentService equipmentService;

    @ApiOperation(value = "Get list of equipment categories")
    @RequestMapping(value = COUNTRY_URL + "/equipment_category", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEquipmentCategories(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,equipmentService.getListOfEquipmentCategories(countryId));
    }

    @ApiOperation(value = "Create a New Equipment in Country")
    @RequestMapping(value = COUNTRY_URL + "/equipment", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addCountryEquipment(@Validated @RequestBody EquipmentDTO equipmentDTO, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,equipmentService.addCountryEquipment(countryId,equipmentDTO));
    }

    @ApiOperation(value = "Update a Country Equipment")
    @RequestMapping(value = COUNTRY_URL + "/equipment/{euipmentId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryEquipment(@Validated @RequestBody EquipmentDTO equipmentDTO, @PathVariable long countryId, @PathVariable long equipmentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,equipmentService.updateEquipment(countryId, equipmentId, equipmentDTO));
    }

    @ApiOperation(value = "Get list of Equipments")
    @RequestMapping(value = COUNTRY_URL + "/equipment", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryEquipment(@PathVariable long countryId,
                                                             @RequestParam(value = "filterText",required = false) String filterText) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,equipmentService.getListOfEquipments(countryId,filterText));
    }

    @ApiOperation(value = "Delete Equipment")
    @RequestMapping(value = COUNTRY_URL + "/equipment/{euipmentId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCountryEquipment(@PathVariable long countryId, @PathVariable long equipmentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,equipmentService.deleteEquipment(countryId, equipmentId));
    }
}

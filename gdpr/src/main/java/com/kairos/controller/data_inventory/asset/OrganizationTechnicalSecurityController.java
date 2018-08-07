package com.kairos.controller.data_inventory.asset;


import com.kairos.controller.master_data.asset_management.TechnicalSecurityController;
import com.kairos.persistance.model.master_data.default_asset_setting.TechnicalSecurityMeasure;
import com.kairos.service.data_inventory.asset.OrganizationTechnicalSecurityMeasureService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationTechnicalSecurityController {


    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalSecurityController.class);

    @Inject
    private OrganizationTechnicalSecurityMeasureService technicalSecurityMeasureService;


    @ApiOperation("add TechnicalSecurityMeasure")
    @PostMapping("/technical_security/add")
    public ResponseEntity<Object> createTechnicalSecurityMeasure(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<TechnicalSecurityMeasure> securityMeasures) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.createTechnicalSecurityMeasure( unitId, securityMeasures.getRequestBody()));

    }


    @ApiOperation("get TechnicalSecurityMeasure by id")
    @GetMapping("/technical_security/{id}")
    public ResponseEntity<Object> getTechnicalSecurityMeasure(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasure(unitId, id));
    }


    @ApiOperation("get all TechnicalSecurityMeasure ")
    @GetMapping("/technical_security/all")
    public ResponseEntity<Object> getAllTechnicalSecurityMeasure(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getAllTechnicalSecurityMeasure(unitId));
    }

    @ApiOperation("get TechnicalSecurityMeasure by name")
    @GetMapping("/technical_security/name")
    public ResponseEntity<Object> getTechnicalSecurityMeasureByName(@PathVariable Long unitId, @RequestParam String name) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasureByName( unitId, name));

    }


    @ApiOperation("delete TechnicalSecurityMeasure  by id")
    @DeleteMapping("/technical_security/delete/{id}")
    public ResponseEntity<Object> deleteTechnicalSecurityMeasure(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.deleteTechnicalSecurityMeasure( unitId, id));

    }

    @ApiOperation("update TechnicalSecurityMeasure by id")
    @PutMapping("/technical_security/update/{id}")
    public ResponseEntity<Object> updateTechnicalSecurityMeasure(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody TechnicalSecurityMeasure securityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.updateTechnicalSecurityMeasure( unitId, id, securityMeasure));

    }

}

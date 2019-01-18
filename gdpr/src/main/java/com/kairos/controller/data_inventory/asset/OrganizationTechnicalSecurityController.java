package com.kairos.controller.data_inventory.asset;


import com.kairos.controller.master_data.asset_management.TechnicalSecurityController;
import com.kairos.dto.gdpr.metadata.TechnicalSecurityMeasureDTO;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class OrganizationTechnicalSecurityController {


    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalSecurityController.class);

    @Inject
    private OrganizationTechnicalSecurityMeasureService technicalSecurityMeasureService;


    @ApiOperation("add TechnicalSecurityMeasure")
    @PostMapping("/technical_security")
    public ResponseEntity<Object> createTechnicalSecurityMeasure(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<TechnicalSecurityMeasureDTO> securityMeasures) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.createTechnicalSecurityMeasure(unitId, securityMeasures.getRequestBody()));

    }


    @ApiOperation("get TechnicalSecurityMeasure by id")
    @GetMapping("/technical_security/{techSecurityMeasureId}")
    public ResponseEntity<Object> getTechnicalSecurityMeasure(@PathVariable Long unitId, @PathVariable Long techSecurityMeasureId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasure(unitId, techSecurityMeasureId));
    }


    @ApiOperation("get all TechnicalSecurityMeasure ")
    @GetMapping("/technical_security")
    public ResponseEntity<Object> getAllTechnicalSecurityMeasure(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getAllTechnicalSecurityMeasure(unitId));
    }

    @ApiOperation("delete TechnicalSecurityMeasure  by id")
    @DeleteMapping("/technical_security/{techSecurityMeasureId}")
    public ResponseEntity<Object> deleteTechnicalSecurityMeasure(@PathVariable Long unitId, @PathVariable Long techSecurityMeasureId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.deleteTechnicalSecurityMeasure(unitId, techSecurityMeasureId));

    }

    @ApiOperation("update TechnicalSecurityMeasure by id")
    @PutMapping("/technical_security/{techSecurityMeasureId}")
    public ResponseEntity<Object> updateTechnicalSecurityMeasure(@PathVariable Long unitId, @PathVariable Long techSecurityMeasureId, @Valid @RequestBody TechnicalSecurityMeasureDTO securityMeasure) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.updateTechnicalSecurityMeasure(unitId, techSecurityMeasureId, securityMeasure));

    }


    @ApiOperation("save technical security And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/technical_security/suggest")
    public ResponseEntity<Object> saveTechnicalSecurityAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<TechnicalSecurityMeasureDTO> technicalSecurityDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.saveAndSuggestTechnicalSecurityMeasures(countryId, unitId, technicalSecurityDTOs.getRequestBody()));

    }

}

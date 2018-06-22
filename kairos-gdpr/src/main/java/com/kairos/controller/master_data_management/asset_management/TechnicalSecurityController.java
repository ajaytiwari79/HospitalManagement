package com.kairos.controller.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.TechnicalSecurityMeasure;
import com.kairos.service.master_data_management.asset_management.TechnicalSecurityMeasureService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
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

import static com.kairos.constants.ApiConstant.API_TECH_SECURITY_MEASURE_URL;
/*
 *
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_TECH_SECURITY_MEASURE_URL)
@Api(API_TECH_SECURITY_MEASURE_URL)
public class TechnicalSecurityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalSecurityController.class);

    @Inject
    private TechnicalSecurityMeasureService technicalSecurityMeasureService;


    @ApiOperation("add TechnicalSecurityMeasure")
    @PostMapping("/add")
    public ResponseEntity<Object> createTechnicalSecurityMeasure(@PathVariable Long countryId,@Valid @RequestBody ValidateListOfRequestBody<TechnicalSecurityMeasure> securityMeasures) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.createTechnicalSecurityMeasure(countryId, securityMeasures.getRequestBody()));

    }


    @ApiOperation("get TechnicalSecurityMeasure by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTechnicalSecurityMeasure(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasure(countryId, id));

    }


    @ApiOperation("get all TechnicalSecurityMeasure ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllTechnicalSecurityMeasure() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getAllTechnicalSecurityMeasure());

    }

    @ApiOperation("get TechnicalSecurityMeasure by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getTechnicalSecurityMeasureByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasureByName(countryId, name));

    }


    @ApiOperation("delete TechnicalSecurityMeasure  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteTechnicalSecurityMeasure(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.deleteTechnicalSecurityMeasure(id));

    }

    @ApiOperation("update TechnicalSecurityMeasure by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateTechnicalSecurityMeasure(@PathVariable BigInteger id,  @Valid @RequestBody TechnicalSecurityMeasure securityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.updateTechnicalSecurityMeasure(id, securityMeasure));

    }


}

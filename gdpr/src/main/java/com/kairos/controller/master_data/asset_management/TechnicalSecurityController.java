package com.kairos.controller.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.TechnicalSecurityMeasure;
import com.kairos.service.master_data.asset_management.TechnicalSecurityMeasureService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;

/*
 *
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class TechnicalSecurityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalSecurityController.class);

    @Inject
    private TechnicalSecurityMeasureService technicalSecurityMeasureService;


    @ApiOperation("add TechnicalSecurityMeasure")
    @PostMapping("/technical_security/add")
    public ResponseEntity<Object> createTechnicalSecurityMeasure(@PathVariable Long countryId, @Valid @RequestBody ValidateListOfRequestBody<TechnicalSecurityMeasure> securityMeasures) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.createTechnicalSecurityMeasure(countryId, securityMeasures.getRequestBody()));

    }


    @ApiOperation("get TechnicalSecurityMeasure by id")
    @GetMapping("/technical_security/{id}")
    public ResponseEntity<Object> getTechnicalSecurityMeasure(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasure(countryId, id));
    }


    @ApiOperation("get all TechnicalSecurityMeasure ")
    @GetMapping("/technical_security/all")
    public ResponseEntity<Object> getAllTechnicalSecurityMeasure(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getAllTechnicalSecurityMeasure(countryId));
    }

    @ApiOperation("get TechnicalSecurityMeasure by name")
    @GetMapping("/technical_security/name")
    public ResponseEntity<Object> getTechnicalSecurityMeasureByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasureByName(countryId, name));

    }


    @ApiOperation("delete TechnicalSecurityMeasure  by id")
    @DeleteMapping("/technical_security/delete/{id}")
    public ResponseEntity<Object> deleteTechnicalSecurityMeasure(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.deleteTechnicalSecurityMeasure(countryId, id));

    }



    public ResponseEntity<Object> updateTechnicalSecurityMeasure(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody TechnicalSecurityMeasure securityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.updateTechnicalSecurityMeasure(countryId, id, securityMeasure));

    }


    @ApiOperation("get All technical security of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping("/technical_security")
    public ResponseEntity<Object> getAllTechnicalMeasurefOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId,@PathVariable Long organizationId,@RequestParam Long parentOrgId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getAllNotInheritedTechnicalSecurityMeasureFromParentOrgAndUnitSecurityMeasure(countryId,parentOrgId,organizationId));
    }



}

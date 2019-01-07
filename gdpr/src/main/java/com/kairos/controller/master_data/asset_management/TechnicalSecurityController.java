package com.kairos.controller.master_data.asset_management;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.TechnicalSecurityMeasureDTO;
import com.kairos.service.master_data.asset_management.TechnicalSecurityMeasureService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;

/*
 *
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class TechnicalSecurityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalSecurityController.class);

    @Inject
    private TechnicalSecurityMeasureService technicalSecurityMeasureService;


    @ApiOperation("add TechnicalSecurityMeasure")
    @PostMapping("/technical_security")
    public ResponseEntity<Object> createTechnicalSecurityMeasure(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<TechnicalSecurityMeasureDTO> securityMeasures) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.createTechnicalSecurityMeasure(countryId, securityMeasures.getRequestBody()));

    }


    @ApiOperation("get TechnicalSecurityMeasure by id")
    @GetMapping("/technical_security/{techSecurityMeasureId}")
    public ResponseEntity<Object> getTechnicalSecurityMeasure(@PathVariable Long countryId, @PathVariable Long techSecurityMeasureId) {
           return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasure(countryId, techSecurityMeasureId));
    }


    @ApiOperation("get all TechnicalSecurityMeasure ")
    @GetMapping("/technical_security")
    public ResponseEntity<Object> getAllTechnicalSecurityMeasure(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getAllTechnicalSecurityMeasure(countryId));
    }

    @ApiOperation("delete TechnicalSecurityMeasure  by id")
    @DeleteMapping("/technical_security/{techSecurityMeasureId}")
    public ResponseEntity<Object> deleteTechnicalSecurityMeasure(@PathVariable Long countryId, @PathVariable Long techSecurityMeasureId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.deleteTechnicalSecurityMeasure(countryId, techSecurityMeasureId));

    }


    @ApiOperation("update Technical security measure by id")
    @PutMapping("/technical_security/{techSecurityMeasureId}")
    public ResponseEntity<Object> updateTechnicalSecurityMeasure(@PathVariable Long countryId, @PathVariable Long techSecurityMeasureId, @Valid @RequestBody TechnicalSecurityMeasureDTO securityMeasure) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.updateTechnicalSecurityMeasure(countryId, techSecurityMeasureId, securityMeasure));

    }

    @ApiOperation("update Suggested status of Technical Security")
    @PutMapping("/technical_security")
    public ResponseEntity<Object> updateSuggestedStatusOfTechnicalSecurityMeasures(@PathVariable Long countryId, @RequestBody Set<BigInteger> techSecurityMeasureIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(techSecurityMeasureIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Security Measure is Not Selected");
        }else   if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.updateSuggestedStatusOfTechnicalSecurityMeasures(countryId, techSecurityMeasureIds, suggestedDataStatus));
    }


}

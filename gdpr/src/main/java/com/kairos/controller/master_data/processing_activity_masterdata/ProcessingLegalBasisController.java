package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.ProcessingLegalBasisDTO;
import com.kairos.service.master_data.processing_activity_masterdata.ProcessingLegalBasisService;
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
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class ProcessingLegalBasisController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingLegalBasisController.class);

    @Inject
    private ProcessingLegalBasisService legalBasisService;


    @ApiOperation("add ProcessingLegalBasis")
    @PostMapping("/legal_basis")
    public ResponseEntity<Object> createProcessingLegalBasis(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<ProcessingLegalBasisDTO> legalBases) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.createProcessingLegalBasis(countryId, legalBases.getRequestBody(),false));

    }


    @ApiOperation("get ProcessingLegalBasis by id")
    @GetMapping("/legal_basis/{processingLegalBasisId}")
    public ResponseEntity<Object> getProcessingLegalBasis(@PathVariable Long countryId, @PathVariable Long processingLegalBasisId) {
      return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasis(countryId, processingLegalBasisId));
    }


    @ApiOperation("get all ProcessingLegalBasis ")
    @GetMapping("/legal_basis")
    public ResponseEntity<Object> getAllProcessingLegalBasis(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getAllProcessingLegalBasis(countryId));
    }

    @ApiOperation("delete ProcessingLegalBasis  by id")
    @DeleteMapping("/legal_basis/{processingLegalBasisId}")
    public ResponseEntity<Object> deleteProcessingLegalBasis(@PathVariable Long countryId, @PathVariable Long processingLegalBasisId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.deleteProcessingLegalBasis(countryId, processingLegalBasisId));

    }


    @ApiOperation("update ProcessingLegalBasis by id")
    @PutMapping("/legal_basis/{processingLegalBasisId}")
    public ResponseEntity<Object> updateProcessingLegalBasis(@PathVariable Long countryId, @PathVariable Long processingLegalBasisId, @Valid @RequestBody ProcessingLegalBasisDTO legalBasis) {
             return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.updateProcessingLegalBasis(countryId, processingLegalBasisId, legalBasis));
    }

    @ApiOperation("update Suggested status of Processing Legal Basis")
    @PutMapping("/legal_basis")
    public ResponseEntity<Object> updateSuggestedStatusOfProcessingLegalBasisList(@PathVariable Long countryId, @RequestBody Set<Long> processingLegalBasisIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(processingLegalBasisIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Legal Basis is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.updateSuggestedStatusOfProcessingLegalBasisList(countryId, processingLegalBasisIds, suggestedDataStatus));
    }


}

package com.kairos.controller.master_data.asset_management;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.OrganizationalSecurityMeasureDTO;
import com.kairos.service.master_data.asset_management.OrganizationalSecurityMeasureService;
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
public class OrganizationalSecurityMeasureController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationalSecurityMeasureController.class);

    @Inject
    private OrganizationalSecurityMeasureService organizationalSecurityMeasureService;


    @ApiOperation("add OrganizationalSecurityMeasure")
    @PostMapping("/organization_security")
    public ResponseEntity<Object> createOrganizationalSecurityMeasure(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<OrganizationalSecurityMeasureDTO> orgSecurityMeasures) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.createOrganizationalSecurityMeasure(countryId, orgSecurityMeasures.getRequestBody(),false));

    }


    @ApiOperation("get OrganizationalSecurityMeasure by id")
    @GetMapping("/organization_security/{orgSecurityMeasureId}")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureById(@PathVariable Long countryId, @PathVariable Long orgSecurityMeasureId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasure(countryId, orgSecurityMeasureId));
    }


    @ApiOperation("get all OrganizationalSecurityMeasure ")
    @GetMapping("/organization_security")
    public ResponseEntity<Object> getAllOrganizationalSecurityMeasure(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getAllOrganizationalSecurityMeasure(countryId));
    }

    @ApiOperation("delete OrganizationalSecurityMeasure  by id")
    @DeleteMapping("/organization_security/{orgSecurityMeasureId}")
    public ResponseEntity<Object> deleteOrganizationalSecurityMeasureById(@PathVariable Long countryId, @PathVariable Long orgSecurityMeasureId) {
          return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.deleteOrganizationalSecurityMeasure(countryId, orgSecurityMeasureId));

    }

    @ApiOperation("update OrganizationalSecurityMeasure by id")
    @PutMapping("/organization_security/{orgSecurityMeasureId}")
    public ResponseEntity<Object> updateOrganizationalSecurityMeasure(@PathVariable Long countryId, @PathVariable Long orgSecurityMeasureId, @Valid @RequestBody OrganizationalSecurityMeasureDTO orgSecurityMeasure) {
           return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.updateOrganizationalSecurityMeasure(countryId, orgSecurityMeasureId, orgSecurityMeasure));
    }


    @ApiOperation("update Suggested status of organizational Security measures ")
    @PutMapping("/organization_security")
    public ResponseEntity<Object> updateSuggestedStatusOFOrgSecurityMeasures(@PathVariable Long countryId, @RequestBody Set<Long> orgSecurityMeasureIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(orgSecurityMeasureIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Security Measure is Not Selected");
        }else  if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.updateSuggestedStatusOfOrganizationalSecurityMeasures(countryId, orgSecurityMeasureIds, suggestedDataStatus));
    }





}

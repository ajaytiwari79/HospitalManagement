package com.kairos.controller.data_inventory.asset;


import com.kairos.dto.gdpr.metadata.OrganizationalSecurityMeasureDTO;
import com.kairos.service.data_inventory.asset.OrganizationOrganizationalSecurityMeasureService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class OrganizationOrganizationalSecurityMeasureController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationOrganizationalSecurityMeasureController.class);

    @Inject
    private OrganizationOrganizationalSecurityMeasureService organizationalSecurityMeasureService;


    @ApiOperation("add OrganizationalSecurityMeasure")
    @PostMapping("/organization_security")
    public ResponseEntity<Object> createOrganizationalSecurityMeasure(@PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<OrganizationalSecurityMeasureDTO> orgSecurityMeasures) {

        if (CollectionUtils.isEmpty(orgSecurityMeasures.getRequestBody())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.createOrganizationalSecurityMeasure(organizationId, orgSecurityMeasures.getRequestBody()));

    }


    @ApiOperation("get OrganizationalSecurityMeasure by id")
    @GetMapping("/organization_security/{orgSecurityMeasureId}")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureById(@PathVariable Long organizationId, @PathVariable Long orgSecurityMeasureId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasure(organizationId, orgSecurityMeasureId));
    }


    @ApiOperation("get all OrganizationalSecurityMeasure ")
    @GetMapping("/organization_security")
    public ResponseEntity<Object> getAllOrganizationalSecurityMeasure(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getAllOrganizationalSecurityMeasure(organizationId));
    }


    @ApiOperation("delete OrganizationalSecurityMeasure  by id")
    @DeleteMapping("/organization_security/{orgSecurityMeasureId}")
    public ResponseEntity<Object> deleteOrganizationalSecurityMeasureById(@PathVariable Long organizationId, @PathVariable Long orgSecurityMeasureId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.deleteOrganizationalSecurityMeasure(organizationId, orgSecurityMeasureId));

    }

    @ApiOperation("update OrganizationalSecurityMeasure by id")
    @PutMapping("/organization_security/{orgSecurityMeasureId}")
    public ResponseEntity<Object> updateOrganizationalSecurityMeasure(@PathVariable Long organizationId, @PathVariable Long orgSecurityMeasureId, @Valid @RequestBody OrganizationalSecurityMeasureDTO orgSecurityMeasure) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.updateOrganizationalSecurityMeasure(organizationId, orgSecurityMeasureId, orgSecurityMeasure));

    }


    @ApiOperation("save Organizational Security measure  And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/organization_security/suggest")
    public ResponseEntity<Object> saveOrganizationalSecurityMeasureAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<OrganizationalSecurityMeasureDTO> orgSecurityMeasureDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.saveAndSuggestOrganizationalSecurityMeasures(countryId, organizationId, orgSecurityMeasureDTOs.getRequestBody()));

    }


}

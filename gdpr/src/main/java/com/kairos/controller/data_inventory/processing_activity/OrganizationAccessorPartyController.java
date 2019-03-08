package com.kairos.controller.data_inventory.processing_activity;

import com.kairos.dto.gdpr.metadata.AccessorPartyDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationAccessorPartyService;
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
class OrganizationAccessorPartyController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationAccessorPartyController.class);

    @Inject
    private OrganizationAccessorPartyService accessorPartyService;


    @ApiOperation("add AccessorParty")
    @PostMapping("/accessor_party")
    public ResponseEntity<Object> createAccessorParty(@PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<AccessorPartyDTO> accessorParties) {
        if (CollectionUtils.isEmpty(accessorParties.getRequestBody())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.createAccessorParty(organizationId, accessorParties.getRequestBody()));

    }


    @ApiOperation("get AccessorParty by id")
    @GetMapping("/accessor_party/{accessorPartyId}")
    public ResponseEntity<Object> getAccessorPartyById(@PathVariable Long organizationId, @PathVariable Long accessorPartyId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAccessorPartyById(organizationId, accessorPartyId));
    }


    @ApiOperation("get all AccessorParty ")
    @GetMapping("/accessor_party")
    public ResponseEntity<Object> getAllAccessorParty(@PathVariable Long organizationId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAllAccessorParty(organizationId));
    }

    @ApiOperation("delete AccessorParty  by id")
    @DeleteMapping("/accessor_party/{accessorPartyId}")
    public ResponseEntity<Object> deleteAccessorParty(@PathVariable Long organizationId, @PathVariable Long accessorPartyId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.deleteAccessorParty(organizationId, accessorPartyId));

    }

    @ApiOperation("update AccessorParty by id")
    @PutMapping("/accessor_party/{accessorPartyId}")
    public ResponseEntity<Object> updateAccessorParty(@PathVariable Long organizationId, @PathVariable Long accessorPartyId, @Valid @RequestBody AccessorPartyDTO accessorParty) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.updateAccessorParty(organizationId, accessorPartyId, accessorParty));

    }


    @ApiOperation("save accessor party And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/accessor_party/suggest")
    public ResponseEntity<Object> saveAccessorPartyAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<AccessorPartyDTO> accessorPartyDTOs) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.saveAndSuggestAccessorParties(countryId, organizationId, accessorPartyDTOs.getRequestBody()));

    }

}

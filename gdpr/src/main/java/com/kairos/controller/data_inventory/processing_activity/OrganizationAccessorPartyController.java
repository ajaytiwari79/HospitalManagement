package com.kairos.controller.data_inventory.processing_activity;

import com.kairos.dto.gdpr.metadata.AccessorPartyDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationAccessorPartyService;
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
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationAccessorPartyController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationAccessorPartyController.class);

    @Inject
    private OrganizationAccessorPartyService accessorPartyService;


    @ApiOperation("add AccessorParty")
    @PostMapping("/accessor_party")
    public ResponseEntity<Object> createAccessorParty(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<AccessorPartyDTO> accessorParties) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.createAccessorParty(unitId, accessorParties.getRequestBody()));

    }


    @ApiOperation("get AccessorParty by id")
    @GetMapping("/accessor_party/{accessorPartyId}")
    public ResponseEntity<Object> getAccessorPartyById(@PathVariable Long unitId, @PathVariable BigInteger accessorPartyId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAccessorPartyById(unitId, accessorPartyId));
    }


    @ApiOperation("get all AccessorParty ")
    @GetMapping("/accessor_party")
    public ResponseEntity<Object> getAllAccessorParty(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAllAccessorParty(unitId));
    }

    @ApiOperation("delete AccessorParty  by id")
    @DeleteMapping("/accessor_party/{accessorPartyId}")
    public ResponseEntity<Object> deleteAccessorParty(@PathVariable Long unitId, @PathVariable BigInteger accessorPartyId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.deleteAccessorParty(unitId, accessorPartyId));

    }

    @ApiOperation("update AccessorParty by id")
    @PutMapping("/accessor_party/{accessorPartyId}")
    public ResponseEntity<Object> updateAccessorParty(@PathVariable Long unitId, @PathVariable BigInteger accessorPartyId, @Valid @RequestBody AccessorPartyDTO accessorParty) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.updateAccessorParty(unitId, accessorPartyId, accessorParty));

    }


    @ApiOperation("save accessor party And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/accessor_party/suggest")
    public ResponseEntity<Object> saveAccessorPartyAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<AccessorPartyDTO> accessorPartyDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.saveAndSuggestAccessorParties(countryId, unitId, accessorPartyDTOs.getRequestBody()));

    }

}

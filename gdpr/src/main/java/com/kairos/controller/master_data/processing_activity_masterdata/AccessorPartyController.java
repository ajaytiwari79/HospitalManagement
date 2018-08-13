package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.gdpr.metadata.AccessorPartyDTO;
import com.kairos.service.master_data.processing_activity_masterdata.AccessorPartyService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;

/*
 *
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class AccessorPartyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessorPartyController.class);

    @Inject
    private AccessorPartyService accessorPartyService;


    @ApiOperation("add AccessorParty")
    @PostMapping("/accessor_party/add")
    public ResponseEntity<Object> createAccessorParty(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<AccessorPartyDTO> accessorParties) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.createAccessorParty(countryId, accessorParties.getRequestBody()));

    }


    @ApiOperation("get AccessorParty by id")
    @GetMapping("/accessor_party/{id}")
    public ResponseEntity<Object> getAccessorParty(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAccessorParty(countryId, id));
    }


    @ApiOperation("get all AccessorParty ")
    @GetMapping("/accessor_party/all")
    public ResponseEntity<Object> getAllAccessorParty(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAllAccessorParty(countryId));
    }

    @ApiOperation("get AccessorParty by name")
    @GetMapping("/accessor_party/name")
    public ResponseEntity<Object> getAccessorPartyByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAccessorPartyByName(countryId, name));

    }


    @ApiOperation("delete AccessorParty  by id")
    @DeleteMapping("/accessor_party/delete/{id}")
    public ResponseEntity<Object> deleteAccessorParty(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.deleteAccessorParty(countryId, id));

    }

    @ApiOperation("update AccessorParty by id")
    @PutMapping("/accessor_party/update/{id}")
    public ResponseEntity<Object> updateAccessorParty(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody AccessorPartyDTO accessorParty) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.updateAccessorParty(countryId, id, accessorParty));
    }

    @ApiOperation("get All AccessorParty of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping("/accessor_party")
    public ResponseEntity<Object> getAllAccessorPartyOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId,@PathVariable Long organizationId,@RequestParam long parentOrgId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAllNotInheritedAccessorPartyFromParentOrgAndUnitAccessorParty(countryId,parentOrgId,organizationId));
    }

}

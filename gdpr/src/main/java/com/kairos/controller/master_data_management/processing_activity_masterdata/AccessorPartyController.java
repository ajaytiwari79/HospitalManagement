package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.AccessorParty;
import com.kairos.service.master_data_management.processing_activity_masterdata.AccessorPartyService;
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

import static com.kairos.constants.ApiConstant.API_ACCESSOR_PARTY_URL;
/*
 *
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_ACCESSOR_PARTY_URL)
@Api(API_ACCESSOR_PARTY_URL)
public class AccessorPartyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessorPartyController.class);

    @Inject
    private AccessorPartyService accessorPartyService;


    @ApiOperation("add AccessorParty")
    @PostMapping("/add")
    public ResponseEntity<Object> createAccessorParty(@PathVariable Long countryId,@PathVariable Long organizationId,@Valid @RequestBody ValidateListOfRequestBody<AccessorParty> accessorParties) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.createAccessorParty(countryId,organizationId,accessorParties.getRequestBody()));

    }


    @ApiOperation("get AccessorParty by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccessorParty(@PathVariable Long countryId,@PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
            return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAccessorParty(countryId,organizationId,id));

    }


    @ApiOperation("get all AccessorParty ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllAccessorParty(@PathVariable Long countryId,@PathVariable Long organizationId) {
         if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAllAccessorParty(countryId,organizationId));

    }

    @ApiOperation("get AccessorParty by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getAccessorPartyByName(@PathVariable Long countryId,@PathVariable Long organizationId, @RequestParam String name) {
         if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAccessorPartyByName(countryId,organizationId,name));

    }


    @ApiOperation("delete AccessorParty  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteAccessorParty(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }   return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.deleteAccessorParty(countryId,organizationId,id));

    }

    @ApiOperation("update AccessorParty by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateAccessorParty(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id, @Valid @RequestBody AccessorParty accessorParty) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }  return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.updateAccessorParty(countryId,organizationId,id, accessorParty));

    }


}

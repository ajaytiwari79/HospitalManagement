package com.kairos.controller.data_inventory.processing_activity;

import com.kairos.gdpr.metadata.AccessorPartyDTO;
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

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationAccessorPartyController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationAccessorPartyController.class);

    @Inject
    private OrganizationAccessorPartyService accessorPartyService;


    @ApiOperation("add AccessorParty")
    @PostMapping("/accessor_party/add")
    public ResponseEntity<Object> createAccessorParty(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<AccessorPartyDTO> accessorParties) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.createAccessorParty(unitId, accessorParties.getRequestBody()));

    }


    @ApiOperation("get AccessorParty by id")
    @GetMapping("/accessor_party/{id}")
    public ResponseEntity<Object> getAccessorPartyById(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAccessorPartyById(unitId, id));
    }


    @ApiOperation("get all AccessorParty ")
    @GetMapping("/accessor_party/all")
    public ResponseEntity<Object> getAllAccessorParty(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAllAccessorParty(unitId));
    }

    @ApiOperation("get AccessorParty by name")
    @GetMapping("/accessor_party/name")
    public ResponseEntity<Object> getAccessorPartyByIdByName(@PathVariable Long unitId, @RequestParam String name) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAccessorPartyByName(unitId, name));

    }


    @ApiOperation("delete AccessorParty  by id")
    @DeleteMapping("/accessor_party/delete/{id}")
    public ResponseEntity<Object> deleteAccessorParty(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.deleteAccessorParty(unitId, id));

    }

    @ApiOperation("update AccessorParty by id")
    @PutMapping("/accessor_party/update/{id}")
    public ResponseEntity<Object> updateAccessorParty(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody AccessorPartyDTO accessorParty) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.updateAccessorParty(unitId, id, accessorParty));

    }


}

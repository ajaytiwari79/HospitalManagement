package com.kairos.controller.master_data.asset_management;


import com.kairos.gdpr.metadata.HostingTypeDTO;
import com.kairos.service.master_data.asset_management.HostingTypeService;
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
public class HostingTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeController.class);

    @Inject
    private HostingTypeService hostingTypeService;


    @ApiOperation("add HostingType")
    @PostMapping("/hosting_type/add")
    public ResponseEntity<Object> createHostingType(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<HostingTypeDTO> hostingTypeDTOs) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.createHostingType(countryId, hostingTypeDTOs.getRequestBody()));

    }


    @ApiOperation("get HostingType by id")
    @GetMapping("/hosting_type/{id}")
    public ResponseEntity<Object> getHostingType(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingType(countryId, id));

    }


    @ApiOperation("get all HostingType ")
    @GetMapping("/hosting_type/all")
    public ResponseEntity<Object> getAllHostingType(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getAllHostingType(countryId));
    }


    @ApiOperation("get HostingType by name")
    @GetMapping("/hosting_type/name")
    public ResponseEntity<Object> getHostingTypeByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingTypeByName(countryId, name));

    }


    @ApiOperation("delete HostingType  by id")
    @DeleteMapping("/hosting_type/delete/{id}")
    public ResponseEntity<Object> deleteHostingType(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.deleteHostingType(countryId, id));

    }

    @ApiOperation("update HostingType by id")
    @PutMapping("/hosting_type/update/{id}")
    public ResponseEntity<Object> updateHostingType(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody HostingTypeDTO hostingType) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.updateHostingType(countryId, id, hostingType));

    }




}

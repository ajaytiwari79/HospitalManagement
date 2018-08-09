package com.kairos.controller.data_inventory.asset;


import com.kairos.controller.master_data.asset_management.HostingTypeController;
import com.kairos.dto.metadata.HostingTypeDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.HostingType;
import com.kairos.service.data_inventory.asset.OrganizationHostingTypeService;
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
public class OrganizationHostingTypeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeController.class);

    @Inject
    private OrganizationHostingTypeService hostingTypeService;


    @ApiOperation("add HostingType")
    @PostMapping("/hosting_type/add")
    public ResponseEntity<Object> createHostingType(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<HostingTypeDTO> hostingTypeDTOs) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.createHostingType(unitId, hostingTypeDTOs.getRequestBody()));

    }


    @ApiOperation("get HostingType by id")
    @GetMapping("/hosting_type/{id}")
    public ResponseEntity<Object> getHostingType(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingType(unitId, id));

    }


    @ApiOperation("get all HostingType ")
    @GetMapping("/hosting_type/all")
    public ResponseEntity<Object> getAllHostingType(@PathVariable Long unitId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getAllHostingType(unitId));
    }


    @ApiOperation("get HostingType by name")
    @GetMapping("/hosting_type/name")
    public ResponseEntity<Object> getHostingTypeByName(@PathVariable Long unitId, @RequestParam String name) {

         if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingTypeByName(unitId, name));

    }


    @ApiOperation("delete HostingType  by id")
    @DeleteMapping("/hosting_type/delete/{id}")
    public ResponseEntity<Object> deleteHostingType(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.deleteHostingType(unitId, id));

    }

    @ApiOperation("update HostingType by id")
    @PutMapping("/hosting_type/update/{id}")
    public ResponseEntity<Object> updateHostingType(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody HostingTypeDTO hostingTypeDTO) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.updateHostingType(unitId, id, hostingTypeDTO));
    }

}

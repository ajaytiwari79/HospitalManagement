package com.kairos.controller.data_inventory.asset;


import com.kairos.controller.master_data.asset_management.HostingTypeController;
import com.kairos.dto.gdpr.metadata.HostingTypeDTO;
import com.kairos.service.data_inventory.asset.OrganizationHostingTypeService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class OrganizationHostingTypeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeController.class);

    @Inject
    private OrganizationHostingTypeService hostingTypeService;


    @ApiOperation("add HostingType")
    @PostMapping("/hosting_type")
    public ResponseEntity<Object> createHostingType(@PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<HostingTypeDTO> hostingTypeDTOs) {

        if (CollectionUtils.isEmpty(hostingTypeDTOs.getRequestBody())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.createHostingType(organizationId, hostingTypeDTOs.getRequestBody()));
    }


    @ApiOperation("get HostingType by id")
    @GetMapping("/hosting_type/{hostingTypeId}")
    public ResponseEntity<Object> getHostingType(@PathVariable Long organizationId, @PathVariable Long hostingTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingType(organizationId, hostingTypeId));

    }


    @ApiOperation("get all HostingType ")
    @GetMapping("/hosting_type")
    public ResponseEntity<Object> getAllHostingType(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getAllHostingType(organizationId));
    }

    @ApiOperation("delete HostingType  by id")
    @DeleteMapping("/hosting_type/{hostingTypeId}")
    public ResponseEntity<Object> deleteHostingType(@PathVariable Long organizationId, @PathVariable BigInteger hostingTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.deleteHostingType(organizationId, hostingTypeId));

    }

    @ApiOperation("update HostingType by id")
    @PutMapping("/hosting_type/{hostingTypeId}")
    public ResponseEntity<Object> updateHostingType(@PathVariable Long organizationId, @PathVariable Long hostingTypeId, @Valid @RequestBody HostingTypeDTO hostingTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.updateHostingType(organizationId, hostingTypeId, hostingTypeDTO));
    }

    @ApiOperation("save Hosting type And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/hosting_type/suggest")
    public ResponseEntity<Object> saveHostingTypeAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<HostingTypeDTO> hostingTypeDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.saveAndSuggestHostingTypes(countryId, organizationId, hostingTypeDTOs.getRequestBody()));

    }

}

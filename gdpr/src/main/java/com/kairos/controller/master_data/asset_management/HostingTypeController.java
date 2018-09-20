package com.kairos.controller.master_data.asset_management;


import com.kairos.enums.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.HostingTypeDTO;
import com.kairos.service.master_data.asset_management.HostingTypeService;
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
public class HostingTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeController.class);

    @Inject
    private HostingTypeService hostingTypeService;


    @ApiOperation("add HostingType")
    @PostMapping("/hosting_type")
    public ResponseEntity<Object> createHostingType(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<HostingTypeDTO> hostingTypeDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.createHostingType(countryId, hostingTypeDTOs.getRequestBody()));

    }


    @ApiOperation("get HostingType by id")
    @GetMapping("/hosting_type/{hostingTypeId}")
    public ResponseEntity<Object> getHostingType(@PathVariable Long countryId, @PathVariable BigInteger hostingTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingType(countryId, hostingTypeId));
    }


    @ApiOperation("get all HostingType ")
    @GetMapping("/hosting_type")
    public ResponseEntity<Object> getAllHostingType(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getAllHostingType(countryId));
    }


    @ApiOperation("delete HostingType  by id")
    @DeleteMapping("/hosting_type/{hostingTypeId}")
    public ResponseEntity<Object> deleteHostingType(@PathVariable Long countryId, @PathVariable BigInteger hostingTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.deleteHostingType(countryId, hostingTypeId));

    }

    @ApiOperation("update HostingType by id")
    @PutMapping("/hosting_type/{hostingTypeId}")
    public ResponseEntity<Object> updateHostingType(@PathVariable Long countryId, @PathVariable BigInteger hostingTypeId, @Valid @RequestBody HostingTypeDTO hostingType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.updateHostingType(countryId, hostingTypeId, hostingType));

    }

    @ApiOperation("update Suggested status of Hosting types")
    @PutMapping("/hosting_type")
    public ResponseEntity<Object> updateSuggestedStatusOfHostingTypes(@PathVariable Long countryId, @RequestBody Set<BigInteger> hostingTypeIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(hostingTypeIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Hosting Type is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.updateSuggestedStatusOfHostingTypes(countryId, hostingTypeIds, suggestedDataStatus));
    }


}

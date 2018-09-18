package com.kairos.controller.data_inventory.asset;


import com.kairos.controller.master_data.asset_management.HostingProviderController;
import com.kairos.dto.gdpr.metadata.HostingProviderDTO;
import com.kairos.service.data_inventory.asset.OrganizationHostingProviderService;
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
public class OrganizationHostingProviderController {


    private static final Logger LOGGER = LoggerFactory.getLogger(HostingProviderController.class);

    @Inject
    private OrganizationHostingProviderService hostingProviderService;


    @ApiOperation("add HostingProvider")
    @PostMapping("/hosting_provider")
    public ResponseEntity<Object> createHostingProvider(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<HostingProviderDTO> hostingProviderDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.createHostingProviders(unitId, hostingProviderDTOs.getRequestBody()));

    }


    @ApiOperation("get HostingProvider by id")
    @GetMapping("/hosting_provider/{hostingProviderId}")
    public ResponseEntity<Object> getHostingProvider(@PathVariable Long unitId, @PathVariable BigInteger hostingProviderId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getHostingProviderById(unitId, hostingProviderId));

    }


    @ApiOperation("get all HostingProvider ")
    @GetMapping("/hosting_provider")
    public ResponseEntity<Object> getAllHostingProvider(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getAllHostingProvider(unitId));
    }


    @ApiOperation("delete HostingProvider  by id")
    @DeleteMapping("/hosting_provider/{hostingProviderId}")
    public ResponseEntity<Object> deleteHostingProvider(@PathVariable Long unitId, @PathVariable BigInteger hostingProviderId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.deleteHostingProvider(unitId, hostingProviderId));

    }

    @ApiOperation("update HostingProvider by id")
    @PutMapping("/hosting_provider/{hostingProviderId}")
    public ResponseEntity<Object> updateHostingProvider(@PathVariable Long unitId, @PathVariable BigInteger hostingProviderId, @Valid @RequestBody HostingProviderDTO hostingProviderDTO) {
       if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.updateHostingProvider(unitId, hostingProviderId, hostingProviderDTO));

    }

    @ApiOperation("save Hosting Provider And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/hosting_provider/suggest")
    public ResponseEntity<Object> saveHostingProviderAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<HostingProviderDTO> hostingProviderDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.saveAndSuggestHostingProviders(countryId, unitId, hostingProviderDTOs.getRequestBody()));

    }


}

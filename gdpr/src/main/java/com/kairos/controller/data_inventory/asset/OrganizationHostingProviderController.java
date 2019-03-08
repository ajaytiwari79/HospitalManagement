package com.kairos.controller.data_inventory.asset;


import com.kairos.controller.master_data.asset_management.HostingProviderController;
import com.kairos.dto.gdpr.metadata.HostingProviderDTO;
import com.kairos.service.data_inventory.asset.OrganizationHostingProviderService;
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
class OrganizationHostingProviderController {


    private static final Logger LOGGER = LoggerFactory.getLogger(HostingProviderController.class);

    @Inject
    private OrganizationHostingProviderService hostingProviderService;


    @ApiOperation("add HostingProvider")
    @PostMapping("/hosting_provider")
    public ResponseEntity<Object> createHostingProvider(@PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<HostingProviderDTO> hostingProviderDTOs) {
        if (CollectionUtils.isEmpty(hostingProviderDTOs.getRequestBody()))
        {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST,false,null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.createHostingProviders(organizationId, hostingProviderDTOs.getRequestBody()));

    }


    @ApiOperation("get HostingProvider by id")
    @GetMapping("/hosting_provider/{hostingProviderId}")
    public ResponseEntity<Object> getHostingProvider(@PathVariable Long organizationId, @PathVariable Long hostingProviderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getHostingProviderById(organizationId, hostingProviderId));

    }


    @ApiOperation("get all HostingProvider ")
    @GetMapping("/hosting_provider")
    public ResponseEntity<Object> getAllHostingProvider(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getAllHostingProvider(organizationId));
    }


    @ApiOperation("delete HostingProvider  by id")
    @DeleteMapping("/hosting_provider/{hostingProviderId}")
    public ResponseEntity<Object> deleteHostingProvider(@PathVariable Long organizationId, @PathVariable Long hostingProviderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.deleteHostingProvider(organizationId, hostingProviderId));

    }

    @ApiOperation("update HostingProvider by id")
    @PutMapping("/hosting_provider/{hostingProviderId}")
    public ResponseEntity<Object> updateHostingProvider(@PathVariable Long organizationId, @PathVariable Long hostingProviderId, @Valid @RequestBody HostingProviderDTO hostingProviderDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.updateHostingProvider(organizationId, hostingProviderId, hostingProviderDTO));

    }

    @ApiOperation("save Hosting Provider And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/hosting_provider/suggest")
    public ResponseEntity<Object> saveHostingProviderAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<HostingProviderDTO> hostingProviderDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.saveAndSuggestHostingProviders(countryId, organizationId, hostingProviderDTOs.getRequestBody()));

    }


}

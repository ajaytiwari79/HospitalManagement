package com.kairos.controller.master_data.asset_management;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.HostingProviderDTO;
import com.kairos.service.master_data.asset_management.HostingProviderService;
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
public class HostingProviderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingProviderController.class);

    @Inject
    private HostingProviderService hostingProviderService;


    @ApiOperation("add HostingProvider")
    @PostMapping("/hosting_provider")
    public ResponseEntity<Object> createHostingProvider(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<HostingProviderDTO> hostingProviderDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.createHostingProviders(countryId, hostingProviderDTOs.getRequestBody()));

    }


    @ApiOperation("get HostingProvider by id")
    @GetMapping("/hosting_provider/{hostingProviderId}")
    public ResponseEntity<Object> getHostingProvider(@PathVariable Long countryId, @PathVariable BigInteger hostingProviderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getHostingProviderById(countryId, hostingProviderId));

    }


    @ApiOperation("get all HostingProvider ")
    @GetMapping("/hosting_provider")
    public ResponseEntity<Object> getAllHostingProvider(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getAllHostingProvider(countryId));

    }

    @ApiOperation("delete HostingProvider  by id")
    @DeleteMapping("/hosting_provider/{hostingProviderId}")
    public ResponseEntity<Object> deleteHostingProvider(@PathVariable Long countryId, @PathVariable BigInteger hostingProviderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.deleteHostingProvider(countryId, hostingProviderId));

    }

    @ApiOperation("update HostingProvider by id")
    @PutMapping("/hosting_provider/{hostingProviderId}")
    public ResponseEntity<Object> updateHostingProvider(@PathVariable Long countryId, @PathVariable BigInteger hostingProviderId, @Valid @RequestBody HostingProviderDTO hostingProviderDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.updateHostingProvider(countryId, hostingProviderId, hostingProviderDTO));

    }

    @ApiOperation("update Suggested status of Hosting provider")
    @PutMapping("/hosting_provider")
    public ResponseEntity<Object> updateSuggestedStatusOfHostingProviders(@PathVariable Long countryId, @RequestBody Set<BigInteger> hostingProviderIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(hostingProviderIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Hosting Provider is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.updateSuggestedStatusOfHostingProviders(countryId, hostingProviderIds, suggestedDataStatus));
    }


}

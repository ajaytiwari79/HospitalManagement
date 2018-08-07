package com.kairos.controller.master_data.asset_management;


import com.kairos.persistance.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.service.master_data.asset_management.HostingProviderService;
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
public class HostingProviderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingProviderController.class);

    @Inject
    private HostingProviderService hostingProviderService;


    @ApiOperation("add HostingProvider")
    @PostMapping("/hosting_provider/add")
    public ResponseEntity<Object> createHostingProvider(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<HostingProvider> hostingProviders) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.createHostingProviders(countryId, hostingProviders.getRequestBody()));

    }


    @ApiOperation("get HostingProvider by id")
    @GetMapping("/hosting_provider/{id}")
    public ResponseEntity<Object> getHostingProvider(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getHostingProviderById(countryId, id));

    }


    @ApiOperation("get all HostingProvider ")
    @GetMapping("/hosting_provider/all")
    public ResponseEntity<Object> getAllHostingProvider(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getAllHostingProvider(countryId));

    }

    @ApiOperation("get hosting provider by name ")
    @GetMapping("/hosting_provider/name")
    public ResponseEntity<Object> getHostingProviderByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getHostingProviderByName(countryId, name));

    }

    @ApiOperation("delete HostingProvider  by id")
    @DeleteMapping("/hosting_provider/delete/{id}")
    public ResponseEntity<Object> deleteHostingProvider(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.deleteHostingProvider(countryId, id));

    }

    @ApiOperation("update HostingProvider by id")
    @PutMapping("/hosting_provider/update/{id}")
    public ResponseEntity<Object> updateHostingProvider(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody HostingProvider hostingProvider) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.updateHostingProvider(countryId, id, hostingProvider));

    }

    @ApiOperation("get All hosting provider  of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping("/hosting_provider")
    public ResponseEntity<Object> getAllHostingProviderOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam Long parentOrgId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getAllNotInheritedHostingProviderFromParentOrgAndUnitHostingProvider(countryId, parentOrgId, organizationId));
    }


}

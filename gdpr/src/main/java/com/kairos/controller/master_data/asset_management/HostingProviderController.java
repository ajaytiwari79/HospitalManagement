package com.kairos.controller.master_data.asset_management;


import com.kairos.persistance.model.master_data.asset_management.HostingProvider;
import com.kairos.service.master_data.asset_management.HostingProviderService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


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
    public ResponseEntity<Object> createHostingProvider(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<HostingProvider> hostingProviders) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.createHostingProviders(countryId, organizationId, hostingProviders.getRequestBody()));

    }


    @ApiOperation("get HostingProvider by id")
    @GetMapping("/hosting_provider/{id}")
    public ResponseEntity<Object> getHostingProvider(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
            return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getHostingProviderById(countryId, organizationId, id));

    }


    @ApiOperation("get all HostingProvider ")
    @GetMapping("/hosting_provider/all")
    public ResponseEntity<Object> getAllHostingProvider(@PathVariable Long countryId, @PathVariable Long organizationId) {
         if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getAllHostingProvider(countryId, organizationId));

    }

    @ApiOperation("get hosting provider by name ")
    @GetMapping("/hosting_provider/name")
    public ResponseEntity<Object> getHostingProviderByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
      if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getHostingProviderByName(countryId, organizationId, name));

    }

    @ApiOperation("delete HostingProvider  by id")
    @DeleteMapping("/hosting_provider/delete/{id}")
    public ResponseEntity<Object> deleteHostingProvider(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        } return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.deleteHostingProvider(countryId, organizationId, id));

    }

    @ApiOperation("update HostingProvider by id")
    @PutMapping("/hosting_provider/update/{id}")
    public ResponseEntity<Object> updateHostingProvider(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody HostingProvider hostingProvider) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        } return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.updateHostingProvider(countryId, organizationId, id, hostingProvider));

    }

    @ApiOperation("get HostingProvider of Unit by id")
    @GetMapping(UNIT_URL+"/hosting_provider/{id}")
    public ResponseEntity<Object> getHostingProviderOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getHostingProviderById(countryId, unitId, id));

    }


    @ApiOperation("get all HostingProvider of Unit")
    @GetMapping(UNIT_URL+"/hosting_provider/all")
    public ResponseEntity<Object> getAllHostingProviderOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getAllHostingProvider(countryId, unitId));

    }


}

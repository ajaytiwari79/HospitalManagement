package com.kairos.controller.cta_wta;

import com.kairos.response.dto.web.WtaDTO;
import com.kairos.service.cta_wta.WTAService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.ApiConstants.PARENT_ORGANIZATION_URL;


/**
 * Created by pawanmandhan on 2/8/17.
 */

@RestController
@RequestMapping(API_V1+PARENT_ORGANIZATION_URL+COUNTRY_URL)
@Api(COUNTRY_URL)
public class WTAController {


    @Inject
    private WTAService wtaService;


    @ApiOperation(value = "Create a New WTA")
    @PostMapping(value = "/wta")
    public ResponseEntity<Map<String, Object>> createWta(@PathVariable long countryId, @RequestBody WtaDTO wta) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.createWta(countryId,wta));
    }

    @ApiOperation(value = "Update WTA")
    @PutMapping(value = "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateWta(@PathVariable long wtaId,@RequestBody WtaDTO wta) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.updateWta(wtaId,wta));
    }

    @ApiOperation(value = "Get WTA")
    @GetMapping(value = "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> getWta(@PathVariable long wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getWta(wtaId));
    }

    @ApiOperation(value = "Remove WTA")
    @DeleteMapping(value = "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> deleteWta(@PathVariable long wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.removeWta(wtaId));
    }

    /*
      *get all WTA by organizationId
      * Created by vipul on 9 august 2017
    */

    @ApiOperation(value = "Get WTA by organization ID")
    @RequestMapping(value = "/wta/ByOrganization", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAByOrganizationId(@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAByOrganizationId(organizationId));
    }

    /*
      *get all WTA by countryId
      * Created by vipul on 10 august 2017
    */

    @ApiOperation(value = "Get WTA by CountryId ID")
    @RequestMapping(value = "/wta/ByCountry", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAByCountryId(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAByCountryId(countryId));
    }

    /*
      *get all WTA by organizationSubType
      * Created by vipul on 11 august 2017
      * http://xyz.example.com/api/v1/organization/71/country/53/organization_type/93/wta
    */

    @ApiOperation(value = "Get WTA by Org subType")
    @RequestMapping(value = "organization_type/{organizationTypeId}/wta/OrganizationSubType", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAByOrganizationSubType(@PathVariable long organizationTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAByOrganizationSubType(organizationTypeId));
    }
/*
* vipul
* api to get all org and suborg with wta's
* */
    @ApiOperation(value = "Get All organization and suborganization based on CountryId")
    @RequestMapping(value = "/OrganizationType", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAWithOrganization(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAWithOrganization(countryId));
    }

    /*
    * vipul
    * api to get all org and sub-org with selected RuleTemplate
    * */
    @ApiOperation(value = "Get All organization and suborganization based on ruleTemplateId")
    @RequestMapping(value = "organization_type/{organizationTypeId}/wta/{wtaId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAWithWTAId(@PathVariable long wtaId,@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAWithWTAId(countryId,wtaId));
    }

}

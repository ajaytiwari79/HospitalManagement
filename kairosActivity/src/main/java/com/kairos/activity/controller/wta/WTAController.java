package com.kairos.activity.controller.wta;


import com.kairos.activity.service.wta.WTAOrganizationService;
import com.kairos.activity.service.wta.WTAService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.wta.WTADTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.*;


/**
 * Created by pawanmandhan on 2/8/17.
 */

@RestController
@RequestMapping(API_V1 + PARENT_ORGANIZATION_URL)
@Api(API_V1 + PARENT_ORGANIZATION_URL)
public class WTAController {


    @Inject
    private WTAService wtaService;
    @Inject
    private WTAOrganizationService wtaOrganizationService;

    @ApiOperation(value = "Create a New WTA")
    @PostMapping(value = COUNTRY_URL + "/wta")
    public ResponseEntity<Map<String, Object>> createWta(@PathVariable long countryId, @Validated @RequestBody WTADTO wta) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, wtaService.createWta(countryId, wta));
    }

    @ApiOperation(value = "Update WTA")
    @PutMapping(value = COUNTRY_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateWta(@PathVariable long countryId, @PathVariable long wtaId, @RequestBody WTADTO wta) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.updateWtaOfCountry(countryId, wtaId, wta));
    }


    @ApiOperation(value = "Get WTA")
    @GetMapping(value = COUNTRY_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> getWta(@PathVariable long wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getWta(wtaId));
    }

    @ApiOperation(value = "Remove WTA")
    @DeleteMapping(value = COUNTRY_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> deleteWta(@PathVariable long wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.removeWta(wtaId));
    }

//    /*
//      *get all WTA by organizationId
//      * Created by vipul on 9 august 2017
//    */
//
//    @ApiOperation(value = "Get WTA by organization ID")
//    @RequestMapping(value = COUNTRY_URL + "/wta/ByOrganization", method = RequestMethod.GET)
//    public ResponseEntity<Map<String, Object>> getAllWTAByOrganizationId(@PathVariable long organizationId) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAByOrganizationId(organizationId));
//    }

    /*
      *get all WTA by countryId
      * Created by vipul on 10 august 2017
    */

    @ApiOperation(value = "Get WTA by CountryId ID")
    @RequestMapping(value = COUNTRY_URL + "/wta/ByCountry", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAByCountryId(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAByCountryId(countryId));
    }

    /*
      * get all available expertise by org Sub type Id
      * Created by vipul on 12 sept 2017
    */

/*    @ApiOperation(value = "Get all expertise which are not unlinked with this subOrg type of country")
    @GetMapping(value = COUNTRY_URL + "/{organizationSubTypeId}/expertises")
    public ResponseEntity<Map<String, Object>> getAllAvailableExpertise(@PathVariable long organizationSubTypeId, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllAvailableExpertise(organizationSubTypeId, countryId));
    }*/

    //TODO need to refactor URL pattern

    /**
     * get all WTA by organizationSubType by using Id
     *
     * @Author vipul
     * @Date 11 august 2017
     */

    @ApiOperation(value = "Get WTA by Organization sub type  by using sub type Id")
    @RequestMapping(value = COUNTRY_URL + "/organization_type/{organizationSubTypeId}/wta/OrganizationSubType", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAByOrganizationSubType(@PathVariable long organizationSubTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAByOrganizationSubType(organizationSubTypeId));
    }

    /**
     * @Author vipul
     * api to get all org and sub-org with wta's
     */
    @ApiOperation(value = "Get All organization and sub-organization based on CountryId")
    @RequestMapping(value = COUNTRY_URL + "/OrganizationType", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAWithOrganization(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAWithOrganization(countryId));
    }

    /**
     * @Author vipul
     * api to get organization type  and sub organization type with wta Id
     * This was used for used to get data for uses of this WTA to other organization
     */
    @ApiOperation(value = "Get All organization and sub organization based on wtaId")
    @RequestMapping(value = COUNTRY_URL + "/organization_type/{organizationTypeId}/wta/{wtaId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAWithWTAId(@PathVariable long wtaId, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.getAllWTAWithWTAId(countryId, wtaId));
    }

    @ApiOperation(value = "link unlink wta with org Type")
    @PutMapping(value = COUNTRY_URL + "/organization_type/{organizationSubTypeId}/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> setWtaWithOrganizationType(@PathVariable long countryId, @PathVariable long wtaId, @PathVariable long organizationSubTypeId, @RequestParam(value = "checked") boolean checked) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.setWtaWithOrganizationType(countryId, wtaId, organizationSubTypeId, checked));
    }


    /**
     * get all WTA by organization Id
     *
     * @Author Vipul on 20 DEC
     */

    @ApiOperation(value = "Get WTA by Org subType")
    @RequestMapping(value = UNIT_URL + "/wta/ByOrganization", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getWTAOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaOrganizationService.getAllWTAByOrganization(unitId));
    }


    @ApiOperation(value = "Remove WTA from organization ")
    @DeleteMapping(value = UNIT_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> deleteWtaFromOrganization(@PathVariable long wtaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaService.removeWta(wtaId));
    }

    @ApiOperation(value = "Update WTA")
    @PutMapping(value = UNIT_URL + "/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateWtaOfOrganization(@PathVariable long unitId, @PathVariable long wtaId, @RequestBody WTADTO wta) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaOrganizationService.updateWtaOfOrganization(unitId, wtaId, wta));
    }


}

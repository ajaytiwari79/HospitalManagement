package com.kairos.controller.organization_service;

import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;


/**
 * Created by prabjot on 16/9/16.
 */
//// TODO: 26/12/16 URL to be refactored later: Country/Organization will be the sequence
@RestController
@RequestMapping(API_ORGANIZATION_URL+COUNTRY_URL)
@Api(API_ORGANIZATION_URL+COUNTRY_URL)
public class OrganizationServiceController {

    @Inject
    OrganizationServiceService organizationServiceService;


    // GET by id
    @RequestMapping(value = "/organization_service/{id}", method = RequestMethod.GET)
    @ApiOperation("Find organization_service by id")
    public ResponseEntity<Map<String, Object>> getOrganizationService(@PathVariable Long id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.getOrganizationServiceById(id));
    }

    // PUT by id
    @RequestMapping(value = "/organization_service/{id}", method = RequestMethod.PUT)
    @ApiOperation("Update a  organization_service by id")
    public ResponseEntity<Map<String, Object>> updateOrganizationService(@PathVariable long id, @RequestBody Map<String, String> data) {
        Map<String,Object> organizationService = organizationServiceService.updateOrganizationService(id, data.get("name"), data.get("description"));
        if (organizationService == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService);
    }

    // DELETE by id
    @RequestMapping(value = "/organization_service/{id}", method = RequestMethod.DELETE)
    @ApiOperation("Delete a  organization_service by id")
    public ResponseEntity<Map<String, Object>> deleteOrganizationService(@PathVariable Long id) {
        if (id != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK
                    , true, organizationServiceService.deleteOrganizationServiceById(id));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }




    // OrganizationService based On OrganizationTypes
    @RequestMapping(value = "/organization_type/{orgTypeId}/organization_service", method = RequestMethod.GET)
    @ApiOperation("get organization sub services by organization type")
    public ResponseEntity<Map<String, Object>> getOrganizationServices(@PathVariable long orgTypeId) {
        List<Object> organizationServices = organizationServiceService.getOrgServicesByOrgType(orgTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServices);
    }


    /// Add and Remove Organization Service Based on  type of Organization
    @RequestMapping(value = "/organization_type/{orgTypeId}/organization_service", method = RequestMethod.POST)
    @ApiOperation("add organization sub services by organization type")
    public ResponseEntity<Map<String, Object>> linkOrgServiceWithOrgType(@PathVariable long orgTypeId, @RequestBody Map<String, Object> reqData) {
        Long servicesId = Long.valueOf(String.valueOf(reqData.get("servicesId")));
        List<Object> isSuccess = organizationServiceService.linkOrgServiceWithOrgType(orgTypeId, servicesId);
        if (isSuccess!=null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, isSuccess);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
    }




    // Organization Services Based on Country
    @RequestMapping(value = "/organization_service", method = RequestMethod.POST)
    @ApiOperation("Create a new organization_service")
    public ResponseEntity<Map<String, Object>> addOrganizationService(@PathVariable long countryId,@Validated @RequestBody OrganizationService organizationService) {
        organizationService = organizationServiceService.createCountryOrganizationService(countryId, organizationService);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService);
    }

    @RequestMapping(value = "/organization_service", method = RequestMethod.GET)
    @ApiOperation("Find all organization_service")
    public ResponseEntity<Map<String, Object>> getAllOrganizationService(@PathVariable Long organizationId, @PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.getAllOrganizationService(countryId));
    }


    @RequestMapping(value = "/organization_service/{id}/sub_service", method = RequestMethod.POST)
    @ApiOperation("Add organization sub services")
    public ResponseEntity<Map<String, Object>> addSubService(@PathVariable long id, @RequestBody OrganizationService subService) {
        Map<String, Object> response = organizationServiceService.addCountrySubService(id, subService);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }


    /**
     *
     * @param orgTypeIds list of organization Sub Type Ids
     * @return list of Organization services and Children SubServices
     */
    @RequestMapping(value = "/organization_type/organization_service", method = RequestMethod.POST)
    @ApiOperation("get organization sub services by organization Type Ids")
    public ResponseEntity<Map<String, Object>> getOrganizationServicesByOrganizationSubTypeIds(@PathVariable Long countryId,@RequestBody Set<Long> orgTypeIds) {
        List<Object> organizationServices = organizationServiceService.getOrgServicesByOrgSubTypesIds(orgTypeIds);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServices);
    }





}

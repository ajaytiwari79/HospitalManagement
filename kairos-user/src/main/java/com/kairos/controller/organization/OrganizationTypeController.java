package com.kairos.controller.organization;

import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.service.organization.OrganizationTypeService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.ApiConstants.PARENT_ORGANIZATION_URL;


/**
 * Created by oodles on 18/10/16.
 */

@RestController
@RequestMapping(API_V1 + PARENT_ORGANIZATION_URL)
@Api(API_V1 + PARENT_ORGANIZATION_URL)
public class OrganizationTypeController {

    @Inject
    private OrganizationTypeService organizationTypeService;
    @Inject
    private OrganizationServiceService organizationServiceService;


    /// CRUD Operation
    @ApiOperation(value = "Get Organization Type by Id")
    @RequestMapping(value = "/organization_type/{organizationTypeId}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getOrganizationTypebyId(@PathVariable Long organizationTypeId) {
        OrganizationType organizationType = organizationTypeService.getOrganizationTypeById(organizationTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationType);
    }

    @ApiOperation(value = "Get all Organization Types")
    @RequestMapping(value = "/organization_type/", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getAllOrganizationTypes() {
        List<OrganizationType> response = organizationTypeService.getAllOrganizationTypes();
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/organization_type/{orgTypeId}/organization_service", method = RequestMethod.GET)
    @ApiOperation("get organization sub services by organization type")
    public ResponseEntity<Map<String, Object>> getOrganizationServices(@PathVariable long orgTypeId) {
        List<Object> organizationServices = organizationServiceService.getOrgServicesByOrgType(orgTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServices);
    }

    @RequestMapping(value = COUNTRY_URL + "/organization_types/hierarchy", method = RequestMethod.POST)
    @ApiOperation("get organization sub services by organization type")
    public ResponseEntity<Map<String, Object>> getOrganizationTypeHierarchy(@PathVariable long countryId,
                                                                            @RequestBody Set<Long> orgTypeIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getOrganizationTypeHierarchy(countryId,orgTypeIds));
    }

    @RequestMapping(value = "/organization_type/{organizationTypeId}/organizations", method = RequestMethod.GET)
    @ApiOperation("Get organizations by organization type id")
    public ResponseEntity<Map<String,Object>> getOrganizationByOrganizationTypeId(@PathVariable long organizationTypeId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getOrganizationByOrganizationTypeId(organizationTypeId));
    }

    @RequestMapping(value = "/organization_service/{organizationServiceId}/assign/organizationTypes", method = RequestMethod.POST)
    @ApiOperation("Linking of organization types with services")
    public ResponseEntity<Map<String,Object>> linkOrganizationTypeWithService(@RequestBody Set<Long> orgTypeId, @PathVariable long organizationServiceId){
        organizationTypeService.linkOrganizationTypeWithService(orgTypeId,organizationServiceId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @RequestMapping(value = "/organization_service/{organizationServiceId}/detach/organizationTypes", method = RequestMethod.DELETE)
    @ApiOperation("detach organization types with services")
    public ResponseEntity<Map<String,Object>> deleteLinkingOfOrganizationTypeAndService(@RequestBody  Set<Long> orgTypeId, @PathVariable long organizationServiceId){
        organizationTypeService.deleteLinkingOfOrganizationTypeAndService(orgTypeId,organizationServiceId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }
}

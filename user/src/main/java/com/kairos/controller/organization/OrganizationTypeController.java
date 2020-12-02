package com.kairos.controller.organization;

import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.service.organization.OrganizationTypeService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.*;


/**
 * Created by oodles on 18/10/16.
 */

@RestController
@RequestMapping(API_V1 )
@Api(API_V1 )
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
        List<OrganizationServiceDTO> organizationServices = organizationServiceService.getOrgServicesByOrgType(orgTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServices);
    }

    @RequestMapping(value = COUNTRY_URL + "/organization_types/hierarchy", method = RequestMethod.POST)
    @ApiOperation("get organization sub services by organization type")
    public ResponseEntity<Map<String, Object>> getOrganizationTypeHierarchy(@PathVariable long countryId,
                                                                            @RequestBody Set<Long> orgTypeIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getOrganizationTypeHierarchy(countryId, orgTypeIds));
    }

    @ApiOperation(value = "Get All organization and sub-organization based on CountryId")
    @RequestMapping(value = COUNTRY_URL + "/OrganizationType", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAWithOrganization(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getAllOrganizationTypeAndSubType(countryId));
    }

    @RequestMapping(value = "/organization_type/{organizationTypeId}/organizations", method = RequestMethod.GET)
    @ApiOperation("Get organizations by organization type id")
    public ResponseEntity<Map<String, Object>> getOrganizationByOrganizationTypeId(@PathVariable long organizationTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getOrganizationByOrganizationTypeId(organizationTypeId));
    }

    @RequestMapping(value = "/organization_service/{organizationServiceId}/assign/organizationTypes", method = RequestMethod.POST)
    @ApiOperation("Linking of organization types with services")
    public ResponseEntity<Map<String, Object>> linkOrganizationTypeWithService(@RequestBody Set<Long> orgTypeId, @PathVariable long organizationServiceId) {
        organizationTypeService.linkOrganizationTypeWithService(orgTypeId, organizationServiceId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @RequestMapping(value = "/organization_service/{organizationServiceId}/detach/organizationTypes", method = RequestMethod.DELETE)
    @ApiOperation("detach organization types with services")
    public ResponseEntity<Map<String, Object>> deleteLinkingOfOrganizationTypeAndService(@RequestBody Set<Long> orgTypeId, @PathVariable long organizationServiceId) {
        organizationTypeService.deleteLinkingOfOrganizationTypeAndService(orgTypeId, organizationServiceId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation(value = "get organization and services")
    @RequestMapping(value = COUNTRY_URL + "/organization_type/all", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getOrgTypeAndOrgServiceshierarchy(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getAllOrganizationTypeAndServiceAndSubServices(countryId));
    }


    @ApiOperation(value = "get organization Type ,Sub Type , Org services and Sub Services")
    @GetMapping(value = UNIT_URL + "/organization_type")
    ResponseEntity<Map<String, Object>> getOrgTypeSubTypesOrganizationServiceAndSubServicesByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getOrgTypesServicesAndSubServicesListByUnitId(unitId));
    }

    @PostMapping(value = COUNTRY_URL + "/organization_type_service/organizations")
    @ApiOperation("Get organizations by organization type ids and service ids")
    public ResponseEntity<Map<String, Object>>  getOrganizationIdsByOrgSubTypeIdsAndSubServiceIds(@RequestBody Map<String,List<Long>> organizationSubTypeAndSubServiceIdMap) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getOrganizationIdsByOrgSubTypeIdsAndSubServiceIds(organizationSubTypeAndSubServiceIdMap.get("organizationSubTypeIds"),organizationSubTypeAndSubServiceIdMap.get("organizationSubServicesIds")));
    }

}

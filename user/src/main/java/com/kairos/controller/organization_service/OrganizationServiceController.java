package com.kairos.controller.organization_service;

import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.*;


/**
 * Created by prabjot on 16/9/16.
 */
//// TODO: 26/12/16 URL to be refactored later: Country/Organization will be the sequence
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class OrganizationServiceController {

    @Inject
    OrganizationServiceService organizationServiceService;


    // GET by id
    @RequestMapping(value = COUNTRY_URL+"/organization_service/{id}", method = RequestMethod.GET)
    @ApiOperation("Find organization_service by id")
    public ResponseEntity<Map<String, Object>> getOrganizationService(@PathVariable Long id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.getOrganizationServiceById(id));
    }

    // PUT by id
    @RequestMapping(value = COUNTRY_URL+"/organization_service/{id}", method = RequestMethod.PUT)
    @ApiOperation("Update a  organization_service by id")
    public ResponseEntity<Map<String, Object>> updateOrganizationService(@PathVariable long id, @PathVariable Long countryId,@RequestBody Map<String, String> data) {
        Map<String,Object> organizationService = organizationServiceService.updateOrganizationService(id, data.get("name"), data.get("description"),countryId);
        if (organizationService == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService);
    }

    // DELETE by id
    @RequestMapping(value = COUNTRY_URL+"/organization_service/{id}", method = RequestMethod.DELETE)
    @ApiOperation("Delete a  organization_service by id")
    public ResponseEntity<Map<String, Object>> deleteOrganizationService(@PathVariable Long id) {
        if (id != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK
                    , true, organizationServiceService.deleteOrganizationServiceById(id));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }




    // OrganizationService based On OrganizationTypes
    @RequestMapping(value = COUNTRY_URL+"/organization_type/{orgTypeId}/organization_service", method = RequestMethod.GET)
    @ApiOperation("get organization sub services by organization type")
    public ResponseEntity<Map<String, Object>> getOrganizationServices(@PathVariable long orgTypeId) {
        List<Map<String,Object>> organizationServices = organizationServiceService.getOrgServicesByOrgType(orgTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServices);
    }


    /// Add and Remove Organization Service Based on  type of Organization
    @RequestMapping(value = COUNTRY_URL+"/organization_type/{orgTypeId}/organization_service", method = RequestMethod.POST)
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
    @RequestMapping(value = COUNTRY_URL+"/organization_service", method = RequestMethod.POST)
    @ApiOperation("Create a new organization_service")
    public ResponseEntity<Map<String, Object>> addOrganizationService(@PathVariable long countryId,@Valid @RequestBody OrganizationService organizationService) {
        organizationService = organizationServiceService.createCountryOrganizationService(countryId, organizationService);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService);
    }

    @RequestMapping(value = COUNTRY_URL+"/organization_service", method = RequestMethod.GET)
    @ApiOperation("Find all organization_service")
    public ResponseEntity<Map<String, Object>> getAllOrganizationService(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.getAllOrganizationService(countryId));
    }


    @RequestMapping(value = COUNTRY_URL+"/organization_service/{id}/sub_service", method = RequestMethod.POST)
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
    @RequestMapping(value = COUNTRY_URL+"/organization_type/organization_service", method = RequestMethod.POST)
    @ApiOperation("get organization sub services by organization Type Ids")
    public ResponseEntity<Map<String, Object>> getOrganizationServicesByOrganizationSubTypeIds(@PathVariable Long countryId,@RequestBody Set<Long> orgTypeIds) {
        List<Object> organizationServices = organizationServiceService.getOrgServicesByOrgSubTypesIds(orgTypeIds);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServices);
    }

    @RequestMapping(value = UNIT_URL+"/get_organisation_services_by_unit", method = RequestMethod.GET)
    @ApiOperation("Find all organization_service by unit")
    public ResponseEntity<Map<String, Object>> getAllOrganizationServiceByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.getAllOrganizationServicesByUnitId(unitId));
    }

    @RequestMapping(value = COUNTRY_URL+"/organization_service/{id}/update_translation", method = RequestMethod.PUT)
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfActivity(@PathVariable Long id, @RequestBody Map<String,TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.updateTranslation(id,translations));
    }

//  Todo please do not remove this commited code I am working On it later
//    @ApiOperation(value = "get translated data")
//    @RequestMapping(value = COUNTRY_URL+"/organization_service/{id}/get_translation_data", method = RequestMethod.GET)
//    public ResponseEntity<Map<String, Object>> getTranslatedData(@PathVariable Long id) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.getTranslatedData(id));
//    }

}

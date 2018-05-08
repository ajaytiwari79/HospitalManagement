package com.kairos.controller.organization;


import com.kairos.persistance.model.organization.OrganizationService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/organization/service")
public class OrganizationServiceController {


    @Inject
    private OrganizationServiceService organizationServiceService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createOrganizationService(@RequestBody OrganizationService organizationService) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.createOrganizationservice(organizationService));
    }


    @RequestMapping(value = "/organizationServiceList", method = RequestMethod.POST)
    public ResponseEntity<Object> getOrganizationServiceList(@RequestBody List<Long> orgTypeList) {

        if (orgTypeList == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization Service List is Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.getOrganizationServiceList(orgTypeList));
    }


}

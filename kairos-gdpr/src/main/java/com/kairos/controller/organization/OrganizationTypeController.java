package com.kairos.controller.organization;


import com.kairos.persistance.model.organization.OrganizationType;
import com.kairos.persistance.repository.organization.OrganizationTypeMongoRepository;
import com.kairos.service.organization.OrganizationTypeService;
import com.kairos.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kairos.constant.ApiConstant.API_ORGANIZATION_TYPE;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(API_ORGANIZATION_TYPE)
public class OrganizationTypeController {


    @Inject
    private OrganizationTypeService organizationTypeService;
    @Inject
    private OrganizationTypeMongoRepository organizationTypeRepository;

    @PostMapping("/create")
    public OrganizationType createOrganizationType(@RequestBody OrganizationType organizationType) {
        return organizationTypeService.createOrganizationType(organizationType);


    }


    @GetMapping("/id")
    public OrganizationType getOrganizationTypeById(@RequestParam Long id) {
        return organizationTypeRepository.findById(id.toString());
    }


    @RequestMapping(value = "/organizationTypeList", method = RequestMethod.POST)
    public ResponseEntity<Object> getOrganizationTypeByList(@RequestBody Set<Long> orgTypeList) {

        if (orgTypeList == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization List is Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getOrganizationTypes(orgTypeList));
    }


}




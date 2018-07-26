package com.kairos.controller.data_inventory;


import com.kairos.dto.data_inventory.OrganizationMetaDataDTO;
import com.kairos.service.common.DataInheritOrganizationLevelService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.*;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
public class DataInheritOrganizationLevelController {


    @Inject
    private DataInheritOrganizationLevelService dataInheritOrganizationLevelService;


    @ApiOperation(value = "inherit Data from Parent organization on the basis of Org Type, sub Type,Category and Sub Category")
    @PostMapping(UNIT_URL + "/inherit")
    public ResponseEntity<Object> inheritDataFromParentOrganization(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable Long unitId, @Valid @RequestBody OrganizationMetaDataDTO organizationMetaDataDTO) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unit id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataInheritOrganizationLevelService.inheritDataFromParentOrganization(countryId, organizationId, unitId, organizationMetaDataDTO));

    }


}

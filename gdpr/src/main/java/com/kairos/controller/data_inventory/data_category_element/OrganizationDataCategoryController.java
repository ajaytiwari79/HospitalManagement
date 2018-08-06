package com.kairos.controller.data_inventory.data_category_element;


import com.kairos.dto.master_data.DataCategoryDTO;
import com.kairos.service.data_inventory.data_category_element.OrganizationDataCategoryService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationDataCategoryController {


    @Inject
    private OrganizationDataCategoryService organizationDataCategoryService;


    @ApiOperation(value = "create Multiple data category with  data Elements")
    @PostMapping("/data_category")
    ResponseEntity<Object> createDataCategoryAndDataElements(@PathVariable Long unitId, @RequestBody @Valid ValidateListOfRequestBody<DataCategoryDTO> dataCategoryDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataCategoryService.createDataCategoryWithDataElements(unitId
                , dataCategoryDTOs.getRequestBody()));
    }


    @ApiOperation(value = "Delete Data category by id")
    @DeleteMapping("/data_category/delete/{dataCategoryId}")
    public ResponseEntity<Object> deleteDataCategoryById(@PathVariable Long unitId, @PathVariable BigInteger dataCategoryId) {


        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataCategoryService.deleteDataCategoryAndDataElement(unitId, dataCategoryId));
    }


}

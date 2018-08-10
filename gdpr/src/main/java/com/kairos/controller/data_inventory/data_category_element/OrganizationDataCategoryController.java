package com.kairos.controller.data_inventory.data_category_element;


import com.kairos.gdpr.master_data.DataCategoryDTO;
import com.kairos.service.data_inventory.data_category_element.OrganizationDataCategoryService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;
import static com.kairos.constants.AppConstant.*;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationDataCategoryController {


    @Inject
    private OrganizationDataCategoryService organizationDataCategoryService;


    @ApiOperation(value = "create Multiple data category with  data Elements")
    @PostMapping("/data_category")
    ResponseEntity<Object> createDataCategoryAndDataElements(@PathVariable Long unitId, @RequestBody @Valid ValidateRequestBodyList<DataCategoryDTO> dataCategoryDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataCategoryService.createDataCategoryWithDataElements(unitId
                , dataCategoryDTOs.getRequestBody()));
    }


    @ApiOperation(value = "Delete Data category by id")
    @DeleteMapping("/data_category/delete/{id}")
    public ResponseEntity<Object> deleteDataCategoryById(@PathVariable Long unitId, @PathVariable BigInteger id) {

        Map<String, Object> result = organizationDataCategoryService.deleteDataCategoryAndDataElement(unitId, id);
        if (result.get(IS_SUCCESS).equals(true)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, result.get(DATA_SUBJECT_LIST));
    }


    @ApiOperation(value = "Delete Data category by id")
    @GetMapping("/data_category/all")
    public ResponseEntity<Object> getAllDataCategoryWithDataElement(@PathVariable Long unitId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, false, organizationDataCategoryService.getAllDataCategoryWithDataElementByUnitId(unitId));
    }

    @ApiOperation(value = "Delete Data category by id")
    @GetMapping("/data_category/{dataCategoryId}")
    public ResponseEntity<Object> getDataCategoryWithDataElementById(@PathVariable Long unitId, @PathVariable BigInteger dataCategoryId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, false, organizationDataCategoryService.getDataCategoryWithDataElementByUnitIdAndId(unitId, dataCategoryId));
    }


}

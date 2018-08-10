package com.kairos.controller.master_data.data_category_element;


import com.kairos.gdpr.master_data.DataCategoryDTO;
import com.kairos.service.master_data.data_category_element.DataCategoryService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class DataCategoryController {


    @Inject
    private DataCategoryService dataCategoryService;


    @ApiOperation("add data category ")
    @PostMapping("/data_category/add")
    public ResponseEntity<Object> addDataCategoryAndDataElement(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody DataCategoryDTO dataCategoryDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.addDataCategoryAndDataElement(countryId, organizationId, dataCategoryDto));
    }

    @ApiOperation("get data category by id with data Elements ")
    @GetMapping("/data_category/{id}")
    public ResponseEntity<Object> getDataCategoryWithDataElements(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.getDataCategoryWithDataElement(countryId, organizationId, id));

    }

    @ApiOperation("get all data category ")
    @GetMapping("/data_category/all")
    public ResponseEntity<Object> getAllDataCategoryWithDataElements(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.getAllDataCategoryWithDataElement(countryId, organizationId));

    }

    @ApiOperation("delete data category by id ")
    @DeleteMapping("/data_category/delete/{id}")
    public ResponseEntity<Object> deleteDataCategory(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.deleteDataCategory(countryId, organizationId, id));

    }

    @ApiOperation("update data category by id ")
    @PutMapping("/data_category/update/{id}")
    public ResponseEntity<Object> updateDataCategoryAndDataElement(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody DataCategoryDTO dataCategoryDto) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.updateDataCategoryAndDataElement(countryId, organizationId, id, dataCategoryDto));

        }

    @ApiOperation("get data category by id with data Elements ")
    @GetMapping(UNIT_URL+"/data_category/{id}")
    public ResponseEntity<Object> getDataCategoryWithDataElementOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.getDataCategoryWithDataElement(countryId, unitId, id));

    }

    @ApiOperation("get all data category ")
    @GetMapping(UNIT_URL+"/data_category/all")
    public ResponseEntity<Object> getAllDataCategoryWithDataElementsOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.getAllDataCategoryWithDataElement(countryId, unitId));

    }



}

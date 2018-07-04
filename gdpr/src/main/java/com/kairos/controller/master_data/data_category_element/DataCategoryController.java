package com.kairos.controller.master_data.data_category_element;


import com.kairos.dto.master_data.DataCategoryDTO;
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

import static com.kairos.constants.ApiConstant.API_DATA_CATEGORY_URL;

@RestController
@RequestMapping(API_DATA_CATEGORY_URL)
@Api(API_DATA_CATEGORY_URL)
public class DataCategoryController {


    @Inject
    private DataCategoryService dataCategoryService;


    @ApiOperation("add data category ")
    @PostMapping("/add")
    public ResponseEntity<Object> addDataCategoryAndDataElement(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody DataCategoryDTO dataCategoryDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.addDataCategoryAndDataElement(countryId, organizationId, dataCategoryDto));
    }

    @ApiOperation("get data category by id with data Elements ")
    @GetMapping("/{id}")
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
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataCategoryWithDataElements(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.getAllDataCategoryWithDataElement(countryId, organizationId));

    }

    @ApiOperation("delete data category by id ")
    @DeleteMapping("/delete/{id}")
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


    @PutMapping("/update/{id}")
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


}

package com.kairos.controller.master_data_management.data_category_element;


import com.kairos.dto.master_data.DataCategoryDto;
import com.kairos.service.master_data_management.data_category_element.DataCategoryService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_DATA_CATEGORY_URL;

@RestController
@RequestMapping(API_DATA_CATEGORY_URL)
@Api(API_DATA_CATEGORY_URL)
public class DataCategoryController {


    @Inject
    private DataCategoryService dataCategoryService;


    @PostMapping("/add")
    public ResponseEntity<Object> addDataCategoryAndDataElement(@PathVariable Long countryId, @Valid @RequestBody DataCategoryDto dataCategoryDto) {
        if (countryId == null) {

            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.addDataCategoryAndDataElement(countryId, dataCategoryDto));


    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataCategoryWithDataElements(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK,true,dataCategoryService.getDataCategoryWithDataElement(countryId,id));

    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataCategoryWithDataElements(@PathVariable Long countryId) {
       if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK,true,dataCategoryService.getAllDataCategoryWithDataElement(countryId));

    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDataCategory(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.deleteDataCategory(countryId, id));

    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateDataCategoryAndDataElement(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody DataCategoryDto dataCategoryDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataCategoryService.updateDataCategoryAndElement(countryId,id, dataCategoryDto));


    }


}

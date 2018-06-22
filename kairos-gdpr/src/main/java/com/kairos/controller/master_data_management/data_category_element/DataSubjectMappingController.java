package com.kairos.controller.master_data_management.data_category_element;


import com.kairos.dto.master_data.DataSubjectMappingDTO;
import com.kairos.service.master_data_management.data_category_element.DataSubjectMappingService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_DATA_SUBJECT_AND_MAPPING_URL;

@RestController
@RequestMapping(API_DATA_SUBJECT_AND_MAPPING_URL)
@Api(API_DATA_SUBJECT_AND_MAPPING_URL)
public class DataSubjectMappingController {


    @Inject
    private DataSubjectMappingService dataSubjectMappingService;


    @PostMapping("/add")
    public ResponseEntity<Object> addDataSubjectAndMapping(@PathVariable Long countryId, @Valid @RequestBody DataSubjectMappingDTO dataSubjectMappingDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.addDataSubjectAndMapping(countryId, dataSubjectMappingDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDataSubjectAndMapping(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.deleteDataSubjectAndMapping(countryId, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataSubjectWithDataCateogryAndElements(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getDataSubjectAndMappingWithData(countryId, id));
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataSubjectWithDataCateogryAndElements(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getAllDataSubjectAndMappingWithData(countryId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateDataSubjectAndMapping(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody DataSubjectMappingDTO dataSubjectMappingDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.updateDataSubjectAndMapping(countryId, id, dataSubjectMappingDto));
    }


}

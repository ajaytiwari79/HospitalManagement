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
    public ResponseEntity<Object> addDataSubjectAndMapping(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody DataSubjectMappingDTO dataSubjectMappingDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.addDataSubjectAndMapping(countryId, organizationId, dataSubjectMappingDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDataSubjectAndMapping(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.deleteDataSubjectAndMapping(countryId, organizationId, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataSubjectWithDataCateogryAndElements(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getDataSubjectAndMappingWithData(countryId, organizationId, id));
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataSubjectWithDataCateogryAndElements(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getAllDataSubjectAndMappingWithData(countryId, organizationId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateDataSubjectAndMapping(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody DataSubjectMappingDTO dataSubjectMappingDto) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.updateDataSubjectAndMapping(countryId, organizationId, id, dataSubjectMappingDto));
    }


}

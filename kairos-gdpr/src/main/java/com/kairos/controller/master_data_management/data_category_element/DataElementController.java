package com.kairos.controller.master_data_management.data_category_element;


import com.kairos.dto.master_data.DataElementDTO;
import com.kairos.persistance.model.master_data_management.data_category_element.DataElement;
import com.kairos.service.master_data_management.data_category_element.DataElementService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_DATA_ELEMENT_URL;

@RestController
@RequestMapping(API_DATA_ELEMENT_URL)
@Api(API_DATA_ELEMENT_URL)
public class DataElementController {


    @Inject
    private DataElementService dataElementService;


    @PostMapping("/add")
    public ResponseEntity<Object> addDataElement(@PathVariable Long countryId,@PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<DataElementDTO> dataElements) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id Cannotbe null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.createDataElements(countryId,organizationId,dataElements.getRequestBody()));

    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataElement(@PathVariable Long countryId,@PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id Cannotbe null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id Cannotbe null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getDataElement(countryId,organizationId,id));

    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataElement(@PathVariable Long countryId,@PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id Cannotbe null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getAllDataElements(countryId,organizationId));

    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDataElement(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id Cannotbe null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id Cannotbe null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.deleteDataElement(countryId,organizationId,id));

    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateDataElement(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id, @Valid @RequestBody DataElement dataElements) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id Cannotbe null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.updateDataElement(countryId,organizationId,id, dataElements));

    }


}

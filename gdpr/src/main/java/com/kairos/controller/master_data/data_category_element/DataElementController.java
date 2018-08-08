package com.kairos.controller.master_data.data_category_element;


import com.kairos.dto.master_data.DataElementDTO;
import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import com.kairos.service.master_data.data_category_element.DataElementService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class DataElementController {


    @Inject
    private DataElementService dataElementService;


    @ApiOperation("create  data Element ")
    @PostMapping("/data_element/add")
    public ResponseEntity<Object> addDataElement(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<DataElementDTO> dataElements) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.createDataElements(countryId, organizationId, dataElements.getRequestBody()));

    }


    @ApiOperation("get data Element by id")
    @GetMapping("/data_element/{id}")
    public ResponseEntity<Object> getDataElement(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getDataElement(countryId, organizationId, id));

    }

    @ApiOperation("get All data Element ")
    @GetMapping("/data_element/all")
    public ResponseEntity<Object> getAllDataElement(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getAllDataElements(countryId, organizationId));

    }

    @ApiOperation("deleted  data element by id ")
    @DeleteMapping("/data_element/delete/{id}")
    public ResponseEntity<Object> deleteDataElement(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.deleteDataElement(countryId, organizationId, id));

    }


    @ApiOperation("update  data Element ")
    @PutMapping("/data_element/update/{id}")
    public ResponseEntity<Object> updateDataElement(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody DataElement dataElements) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.updateDataElement(countryId, organizationId, id, dataElements));
    }

    @ApiOperation("get data Element of unit by id")
    @GetMapping(UNIT_URL+"/data_element/{id}")
    public ResponseEntity<Object> getDataElementOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getDataElement(countryId, unitId, id));

    }

    @ApiOperation("get All data Element of unit ")
    @GetMapping(UNIT_URL+"/data_element/all")
    public ResponseEntity<Object> getAllDataElementOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getAllDataElements(countryId, unitId));

    }


}

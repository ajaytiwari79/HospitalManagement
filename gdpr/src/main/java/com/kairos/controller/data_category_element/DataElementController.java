package com.kairos.controller.data_category_element;


import com.kairos.dto.gdpr.master_data.DataElementDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import com.kairos.response.dto.master_data.data_mapping.DataElementBasicResponseDTO;
import com.kairos.service.data_subject_management.DataElementService;
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
import java.util.List;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class DataElementController {


    @Inject
    private DataElementService dataElementService;


    @ApiOperation("country ,save data Element")
    @PostMapping(COUNTRY_URL + "/data_element")
    public ResponseEntity<Object> saveDataElementAtCountryLevel(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<DataElementDTO> dataElements) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.createDataElements(countryId, false, dataElements.getRequestBody(), null));

    }


    @ApiOperation("country ,get data Element by id")
    @GetMapping(COUNTRY_URL + "/data_element/{dataElementId}")
    public ResponseEntity<ResponseDTO<DataElement>> getCountryDataElementById(@PathVariable Long countryId, @PathVariable BigInteger dataElementId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataElementService.getDataElementById(dataElementId));

    }

    @ApiOperation("country, get All data Element ")
    @GetMapping(COUNTRY_URL + "/data_element")
    public ResponseEntity<ResponseDTO<List<DataElement>>> getAllCountryDataElement(@PathVariable Long countryId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataElementService.getAllDataElementByCountryId(countryId));

    }

    @ApiOperation("country, deleted  data element by id ")
    @DeleteMapping(COUNTRY_URL + "/data_element/{dataElementId}")
    public ResponseEntity<Object> deleteDataElement(@PathVariable Long countryId, @PathVariable BigInteger dataElementId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.deleteDataElementById(dataElementId));

    }


    @ApiOperation("organization ,create  data Element ")
    @PostMapping(UNIT_URL + "/data_element")
    public ResponseEntity<Object> saveOrganizationDataElement(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<DataElementDTO> dataElements) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.createDataElements(unitId, true, dataElements.getRequestBody(), null));

    }


    @ApiOperation("organization ,get data Element by id")
    @GetMapping(UNIT_URL + "/data_element/{dataElementId}")
    public ResponseEntity<ResponseDTO<DataElement>> getOrganizationDataElementById(@PathVariable Long unitId, @PathVariable BigInteger dataElementId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataElementService.getDataElementById(dataElementId));

    }

    @ApiOperation("organization, get All data Element ")
    @GetMapping(UNIT_URL + "/data_element")
    public ResponseEntity<ResponseDTO<List<DataElement>>> getAllOrganizationDataElement(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataElementService.getAllDataElementByUnitId(unitId));

    }

    @ApiOperation("organization, deleted  data element by id ")
    @DeleteMapping(UNIT_URL + "/data_element/{dataElementId}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteOrganizationDataElementById(@PathVariable Long unitId, @PathVariable BigInteger dataElementId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataElementService.deleteDataElementById(dataElementId));

    }


}

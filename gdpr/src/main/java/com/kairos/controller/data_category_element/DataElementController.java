package com.kairos.controller.data_category_element;


import com.kairos.dto.gdpr.master_data.DataElementDTO;
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

import static com.kairos.constants.ApiConstant.*;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
class DataElementController {


    @Inject
    private DataElementService dataElementService;


    @ApiOperation("country ,save data Element")
    @PostMapping(COUNTRY_URL + "/data_element")
    public ResponseEntity<Object> saveDataElementAtCountryLevel(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<DataElementDTO> dataElements) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.createDataElements(countryId, false, dataElements.getRequestBody()));

    }



    @ApiOperation("organization ,create  data Element ")
    @PostMapping(UNIT_URL + "/data_element")
    public ResponseEntity<Object> saveOrganizationDataElement(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<DataElementDTO> dataElements) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.createDataElements(unitId, true, dataElements.getRequestBody()));

    }

}

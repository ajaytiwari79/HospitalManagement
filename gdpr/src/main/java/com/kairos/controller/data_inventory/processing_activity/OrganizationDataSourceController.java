package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.dto.gdpr.metadata.DataSourceDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationDataSourceService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class OrganizationDataSourceController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationDataSourceController.class);

    @Inject
    private OrganizationDataSourceService dataSourceService;


    @ApiOperation("add dataSource")
    @PostMapping("/data_source")
    public ResponseEntity<Object> createDataSource(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<DataSourceDTO> dataSource) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.createDataSource(unitId, dataSource.getRequestBody()));

    }


    @ApiOperation("get dataSource by id")
    @GetMapping("/data_source/{dataSourceId}")
    public ResponseEntity<Object> getDataSource(@PathVariable Long unitId, @PathVariable Long dataSourceId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSource(unitId, dataSourceId));
    }


    @ApiOperation("get all dataSource ")
    @GetMapping("/data_source")
    public ResponseEntity<Object> getAllDataSource(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllDataSource(unitId));
    }


    @ApiOperation("delete dataSource  by id")
    @DeleteMapping("/data_source/{dataSourceId}")
    public ResponseEntity<Object> deleteDataSource(@PathVariable Long unitId, @PathVariable Long dataSourceId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.deleteDataSource(unitId, dataSourceId));

    }

    @ApiOperation("update dataSource by id")
    @PutMapping("/data_source/{dataSourceId}")
    public ResponseEntity<Object> updateDataSource(@PathVariable Long unitId, @PathVariable Long dataSourceId, @Valid @RequestBody DataSourceDTO dataSource) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.updateDataSource(unitId, dataSourceId, dataSource));

    }


    @ApiOperation("save data Source And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/data_source/suggest")
    public ResponseEntity<Object> saveDataSourceAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<DataSourceDTO> dataSourceDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.saveAndSuggestDataSources(countryId, unitId, dataSourceDTOs.getRequestBody()));

    }


}

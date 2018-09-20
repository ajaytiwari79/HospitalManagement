package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.enums.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.DataSourceDTO;
import com.kairos.service.master_data.processing_activity_masterdata.DataSourceService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;

/*
 *
 *  created by bobby 19/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class DataSourceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceController.class);

    @Inject
    private DataSourceService dataSourceService;


    @ApiOperation("add dataSource")
    @PostMapping("/data_source")
    public ResponseEntity<Object> createDataSource(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<DataSourceDTO> dataSource) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.createDataSource(countryId, dataSource.getRequestBody()));

    }


    @ApiOperation("get dataSource by id")
    @GetMapping("/data_source/{dataSourceId}")
    public ResponseEntity<Object> getDataSource(@PathVariable Long countryId, @PathVariable BigInteger dataSourceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSource(countryId, dataSourceId));
    }


    @ApiOperation("get all dataSource ")
    @GetMapping("/data_source")
    public ResponseEntity<Object> getAllDataSource(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllDataSource(countryId));
    }

    @ApiOperation("delete dataSource  by id")
    @DeleteMapping("/data_source/{dataSourceId}")
    public ResponseEntity<Object> deleteDataSource(@PathVariable Long countryId, @PathVariable BigInteger dataSourceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.deleteDataSource(countryId, dataSourceId));

    }

    @ApiOperation("update dataSource by id")
    @PutMapping("/data_source/{dataSourceId}")
    public ResponseEntity<Object> updateDataSource(@PathVariable Long countryId, @PathVariable BigInteger dataSourceId, @Valid @RequestBody DataSourceDTO dataSource) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.updateDataSource(countryId, dataSourceId, dataSource));

    }

    @ApiOperation("update Suggested status of Data Sources ")
    @PutMapping("/data_source")
    public ResponseEntity<Object> updateSuggestedStatusOfDataSources(@PathVariable Long countryId, @RequestBody Set<BigInteger> dataSourceIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(dataSourceIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Data Source is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.updateSuggestedStatusOfDataSourceList(countryId, dataSourceIds, suggestedDataStatus));
    }


}

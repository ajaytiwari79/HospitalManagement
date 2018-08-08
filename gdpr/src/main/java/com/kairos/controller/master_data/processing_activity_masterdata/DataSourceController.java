package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.persistance.model.master_data.default_proc_activity_setting.DataSource;
import com.kairos.service.master_data.processing_activity_masterdata.DataSourceService;
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
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;

/*
 *
 *  created by bobby 19/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class DataSourceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceController.class);

    @Inject
    private DataSourceService dataSourceService;


    @ApiOperation("add dataSource")
    @PostMapping("/data_source/add")
    public ResponseEntity<Object> createDataSource(@PathVariable Long countryId,@Valid @RequestBody ValidateRequestBodyList<DataSource> dataSource) {
       if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.createDataSource(countryId,dataSource.getRequestBody()));

    }


    @ApiOperation("get dataSource by id")
    @GetMapping("/data_source/{id}")
    public ResponseEntity<Object> getDataSource(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
            return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSource(countryId,id));
    }


    @ApiOperation("get all dataSource ")
    @GetMapping("/data_source/all")
    public ResponseEntity<Object> getAllDataSource(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllDataSource(countryId));
    }

    @ApiOperation("get dataSource by name")
    @GetMapping("/data_source/name")
    public ResponseEntity<Object> getDataSourceByName(@PathVariable Long countryId,@RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSourceByName(countryId,name));

    }


    @ApiOperation("delete dataSource  by id")
    @DeleteMapping("/data_source/delete/{id}")
    public ResponseEntity<Object> deleteDataSource(@PathVariable Long countryId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }    return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.deleteDataSource(countryId,id));

    }

    @ApiOperation("update dataSource by id")
    @PutMapping("/data_source/update/{id}")
    public ResponseEntity<Object> updateDataSource(@PathVariable Long countryId,@PathVariable BigInteger id, @Valid @RequestBody DataSource dataSource) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.updateDataSource(countryId,id, dataSource));

    }



    @ApiOperation("get All Data source of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping("/data_source")
    public ResponseEntity<Object> getAllDataSourceOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId,@PathVariable Long organizationId,@RequestParam Long parentOrgId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllNotInheritedDataSourceFromParentOrgAndUnitDataSource(countryId,parentOrgId,organizationId));
    }


}

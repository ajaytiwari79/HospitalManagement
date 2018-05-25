package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSource;
import com.kairos.service.master_data_management.processing_activity_masterdata.DataSourceService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_DATASOURCE_URL;
/*
 *
 *  created by bobby 19/5/2018
 * */


@RestController
@RequestMapping(API_DATASOURCE_URL)
@Api(API_DATASOURCE_URL)
@CrossOrigin
public class DataSourceController {


    @Inject
    private DataSourceService dataSourceService;


    @ApiOperation("add dataSource")
    @PostMapping("/add")
    public ResponseEntity<Object> createDataSource(@PathVariable Long countryId,@RequestBody List<DataSource> dataSource) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.createDataSource(countryId,dataSource));

    }


    @ApiOperation("get dataSource by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataSource(@PathVariable Long countryId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSource(countryId,id));

    }


    @ApiOperation("get all dataSource ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataSource() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllDataSource());

    }

    @ApiOperation("get dataSource by name")
    @GetMapping("/")
    public ResponseEntity<Object> getDataSourceByName(@PathVariable Long countryId,@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSourceByName(countryId,name));

    }


    @ApiOperation("delete dataSource  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDataSource(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.deleteDataSource(id));

    }

    @ApiOperation("update dataSource by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateDataSource(@PathVariable BigInteger id, @RequestBody DataSource dataSource) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.updateDataSource(id,dataSource));

    }


}

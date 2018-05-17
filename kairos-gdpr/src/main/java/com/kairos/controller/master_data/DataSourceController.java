package com.kairos.controller.master_data;


import com.kairos.persistance.model.master_data.DataSource;
import com.kairos.service.master_data.DataSourceService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_DATASOURCE_URL;


@RestController
@RequestMapping(API_DATASOURCE_URL)
@Api(API_DATASOURCE_URL)
public class DataSourceController {


    @Inject
    private DataSourceService dataSourceService;


    @ApiOperation("add dataSource")
    @PostMapping("/add")
    public ResponseEntity<Object> createDataSource(@RequestParam String dataSource) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.createDataSource(dataSource));

    }


    @ApiOperation("get dataSource by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getDataSourceById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSourceById(id));

    }


    @ApiOperation("get all dataSource ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataSource() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllDataSource());

    }


    @ApiOperation("delete dataSource  by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteDataSourceById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.deleteDataSourceById(id));

    }

    @ApiOperation("update dataSource by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateDataSource(@PathVariable BigInteger id, @RequestParam String dataSource) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.updateDataSource(id, dataSource));

    }


}

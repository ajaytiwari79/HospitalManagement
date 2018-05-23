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


@RestController
@RequestMapping(API_DATASOURCE_URL)
@Api(API_DATASOURCE_URL)
@CrossOrigin
public class DataSourceController {


    @Inject
    private DataSourceService dataSourceService;


    @ApiOperation("add dataSource")
    @PostMapping("/add")
    public ResponseEntity<Object> createDataSource(@RequestBody List<DataSource> dataSource) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.createDataSource(dataSource));

    }


    @ApiOperation("get dataSource by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataSource(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSource(id));

    }


    @ApiOperation("get all dataSource ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataSource() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllDataSource());

    }

    @ApiOperation("get dataSource by name")
    @GetMapping("/")
    public ResponseEntity<Object> getDataSourceByName(@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSourceByName(name));

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

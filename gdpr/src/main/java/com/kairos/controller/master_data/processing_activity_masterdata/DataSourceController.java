package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.persistance.model.master_data.default_proc_activity_setting.DataSource;
import com.kairos.service.master_data.processing_activity_masterdata.DataSourceService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
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
import static com.kairos.constants.ApiConstant.UNIT_URL;

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
    public ResponseEntity<Object> createDataSource(@PathVariable Long countryId,@PathVariable Long organizationId,@Valid @RequestBody ValidateListOfRequestBody<DataSource> dataSource) {
       if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.createDataSource(countryId,organizationId,dataSource.getRequestBody()));

    }


    @ApiOperation("get dataSource by id")
    @GetMapping("/data_source/{id}")
    public ResponseEntity<Object> getDataSource(@PathVariable Long countryId,@PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
            return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSource(countryId,organizationId,id));
    }


    @ApiOperation("get all dataSource ")
    @GetMapping("/data_source/all")
    public ResponseEntity<Object> getAllDataSource(@PathVariable Long countryId,@PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllDataSource(countryId,organizationId));
    }

    @ApiOperation("get dataSource by name")
    @GetMapping("/data_source/name")
    public ResponseEntity<Object> getDataSourceByName(@PathVariable Long countryId,@PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSourceByName(countryId,countryId,name));

    }


    @ApiOperation("delete dataSource  by id")
    @DeleteMapping("/data_source/delete/{id}")
    public ResponseEntity<Object> deleteDataSource(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }  return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.deleteDataSource(countryId,organizationId,id));

    }

    @ApiOperation("update dataSource by id")
    @PutMapping("/data_source/update/{id}")
    public ResponseEntity<Object> updateDataSource(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id, @Valid @RequestBody DataSource dataSource) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }  return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.updateDataSource(countryId,organizationId,id, dataSource));

    }



    @ApiOperation("get All Data source of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping(UNIT_URL+"/inherit/data_source")
    public ResponseEntity<Object> getAllDataSourceOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllNotInheritedDataSourceFromParentOrgAndUnitDataSource(countryId,organizationId,unitId));
    }



    @ApiOperation("get dataSource of Unit by id")
    @GetMapping(UNIT_URL+"/data_source/{id}")
    public ResponseEntity<Object> getDataSourceOfUnitById(@PathVariable Long countryId,@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getDataSource(countryId,unitId,id));
    }


    @ApiOperation("get all dataSource of Unit")
    @GetMapping(UNIT_URL+"/data_source/all")
    public ResponseEntity<Object> getAllDataSourceOfUnit(@PathVariable Long countryId,@PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSourceService.getAllDataSource(countryId,unitId));
    }



}

package com.kairos.controller.activity;


import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@RequestMapping(API_ORGANIZATION_URL+COUNTRY_URL+"/timeType")
public class TimeTypeController {


    @Inject private TimeTypeService timeTypeService;


    @ApiOperation("Create TimeType")
    @PostMapping(value = "/")
    public ResponseEntity<Map<String, Object>> createTimeType(@RequestBody List<TimeTypeDTO> timeTypeDTOS,@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.createTimeType(timeTypeDTOS,countryId));
    }


    @ApiOperation("update TimeType")
    @PutMapping(value = "/")
    public ResponseEntity<Map<String, Object>> updateTimeType(@RequestBody List<TimeTypeDTO> timeTypeDTOS,@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.updateTimeType(timeTypeDTOS,countryId));
    }

    @ApiOperation("getAll TimeType")
    @GetMapping(value = "/")
    public ResponseEntity<Map<String, Object>> getAllTimeType(@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getAllTimeType(null,countryId));
    }


  /*  @ApiOperation("getOne TimeType")
    @GetMapping(value = "/{timeTypeId}")
    public ResponseEntity<Map<String, Object>> getOneTimeType(@PathVariable BigInteger timeTypeId,@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getTimeTypeById(timeTypeId,countryId));
    }*/

    @ApiOperation("delete TimeType")
    @DeleteMapping(value = "/{timeTypeId}")
    public ResponseEntity<Map<String, Object>> deleteTimeType(@PathVariable BigInteger timeTypeId,@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.deleteTimeType(timeTypeId,countryId));
    }

    @ApiOperation("Create Default  TimeType")
    @PostMapping(value = "/default")
    public ResponseEntity<Map<String, Object>> createDefaultTimeType(@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.createDefaultTimeType(countryId));
    }


}

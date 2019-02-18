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
@RequestMapping(API_V1 +COUNTRY_URL+"/timeType")
public class TimeTypeController {


    @Inject private TimeTypeService timeTypeService;

    @ApiOperation("Create a TimeType")
    @PostMapping(value = "/")
    public ResponseEntity<Map<String, Object>> createTimeType(@RequestBody List<TimeTypeDTO> timeTypeDTOS,@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.createTimeType(timeTypeDTOS,countryId));
    }

    @ApiOperation("Update a TimeType")
    @PutMapping(value = "/")
    public ResponseEntity<Map<String, Object>> updateTimeType(@RequestBody TimeTypeDTO timeTypeDTO,@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.updateTimeType(timeTypeDTO, countryId));
    }

    @ApiOperation("Get All TimeTypes")
    @GetMapping(value = "/")
    public ResponseEntity<Map<String, Object>> getAllTimeType(@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getAllTimeType(null,countryId));
    }

    @ApiOperation("Delete a TimeType")
    @DeleteMapping(value = "/{timeTypeId}")
    public ResponseEntity<Map<String, Object>> deleteTimeType(@PathVariable BigInteger timeTypeId,@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.deleteTimeType(timeTypeId,countryId));
    }

    @ApiOperation("Create Default TimeTypes for a Country")
    @PostMapping(value = "/default")
    public ResponseEntity<Map<String, Object>> createDefaultTimeTypes(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.createDefaultTimeTypes(countryId));
    }


}

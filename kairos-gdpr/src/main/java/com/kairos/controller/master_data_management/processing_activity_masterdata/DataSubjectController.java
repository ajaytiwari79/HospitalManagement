package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSubject;
import com.kairos.service.master_data_management.processing_activity_masterdata.DataSubjectService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_DATASUBJECT_URL;
/*
 *
 *  created by bobby 19/5/2018
 * */


@RestController
@RequestMapping(API_DATASUBJECT_URL)
@Api(API_DATASUBJECT_URL)
public class DataSubjectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSubjectController.class);

    @Inject
    private DataSubjectService dataSubjectService;


    @ApiOperation("add DataSubject")
    @PostMapping("/add")
    public ResponseEntity<Object> createDataSubject(@PathVariable Long countryId, @RequestBody List<DataSubject> dataSubjects) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.createDataSubject(countryId, dataSubjects));

    }


    @ApiOperation("get DataSubject by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataSubject(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getDataSubject(countryId, id));

    }


    @ApiOperation("get all DataSubject")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataSubject() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getAllDataSubject());

    }

    @ApiOperation("get dataSource by name")
    @GetMapping("/")
    public ResponseEntity<Object> getDataSubjectByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getDataSubjectByName(countryId, name));

    }


    @ApiOperation("delete DataSubject by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDataSubject(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.deleteDataSubject(id));

    }

    @ApiOperation("update DataSubject by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateDataSubject(@PathVariable BigInteger id, @RequestBody DataSubject dataSubject) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.updateDataSubject(id, dataSubject));

    }


}

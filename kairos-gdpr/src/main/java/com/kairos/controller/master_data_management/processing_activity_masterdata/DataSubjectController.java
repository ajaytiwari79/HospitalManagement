package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSubject;
import com.kairos.service.master_data_management.processing_activity_masterdata.DataSubjectService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_DATASUBJECT_URL;


@RestController
@RequestMapping(API_DATASUBJECT_URL)
@Api(API_DATASUBJECT_URL)
@CrossOrigin
public class DataSubjectController {


    @Inject
    private DataSubjectService dataSubjectService;


    @ApiOperation("add DataSubject")
    @PostMapping("/add")
    public ResponseEntity<Object> createDataSubject(@RequestBody List<DataSubject> dataSubjects) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.createDataSubject(dataSubjects));

    }


    @ApiOperation("get DataSubject by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataSubject(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getDataSubject(id));

    }


    @ApiOperation("get all DataSubject")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataSubject() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getAllDataSubject());

    }

    @ApiOperation("get dataSource by name")
    @GetMapping("/")
    public ResponseEntity<Object> getDataSubjectByName(@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getDataSubjectByName(name));

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

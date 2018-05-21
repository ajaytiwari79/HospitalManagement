package com.kairos.controller.master_data;


import com.kairos.service.master_data.DataSubjectService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_DATASUBJECT_URL;


@RestController
@RequestMapping(API_DATASUBJECT_URL)
@Api(API_DATASUBJECT_URL)
public class DataSubjectController {


    @Inject
    private DataSubjectService dataSubjectService;


    @ApiOperation("add DataSubject")
    @PostMapping("/add")
    public ResponseEntity<Object> createDataSubject(@RequestParam String dataSubject) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.createDataSubject(dataSubject));

    }


    @ApiOperation("get DataSubject by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getDataSubjectById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getDataSubjectById(id));

    }


    @ApiOperation("get all DataSubject")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataSubject() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getAllDataSubject());

    }


    @ApiOperation("delete DataSubject by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteDataSubjectById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.deleteDataSubjectById(id));

    }

    @ApiOperation("update DataSubject by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateDataSubject(@PathVariable BigInteger id, @RequestParam String dataSubject) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.updateDataSubject(id, dataSubject));

    }


}

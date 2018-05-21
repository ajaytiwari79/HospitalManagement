package com.kairos.controller.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.TechnicalSecurityMeasure;
import com.kairos.service.master_data_management.asset_management.TechnicalSecurityMeasureService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_TECH_SECURITY_MEASURE_URL;

@RestController
@RequestMapping(API_TECH_SECURITY_MEASURE_URL)
@Api(API_TECH_SECURITY_MEASURE_URL)
@CrossOrigin
public class TechnicalSecurityController {

    @Inject
    private TechnicalSecurityMeasureService technicalSecurityMeasureService;


    @ApiOperation("add TechnicalSecurityMeasure")
    @PostMapping("/add")
    public ResponseEntity<Object> createTechnicalSecurityMeasure(@RequestBody List<TechnicalSecurityMeasure> securityMeasures) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.createTechnicalSecurityMeasure(securityMeasures));

    }


    @ApiOperation("get TechnicalSecurityMeasure by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getTechnicalSecurityMeasure(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasure(id));

    }


    @ApiOperation("get all TechnicalSecurityMeasure ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllTechnicalSecurityMeasure() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getAllTechnicalSecurityMeasure());

    }

    @ApiOperation("get TechnicalSecurityMeasure by name")
    @GetMapping("")
    public ResponseEntity<Object> getTechnicalSecurityMeasureByName(@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasureByName(name));

    }


    @ApiOperation("delete TechnicalSecurityMeasure  by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteTechnicalSecurityMeasure(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.deleteTechnicalSecurityMeasure(id));

    }

    @ApiOperation("update TechnicalSecurityMeasure by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateTechnicalSecurityMeasure(@PathVariable BigInteger id, @RequestBody TechnicalSecurityMeasure securityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.updateTechnicalSecurityMeasure(id, securityMeasure));

    }


}

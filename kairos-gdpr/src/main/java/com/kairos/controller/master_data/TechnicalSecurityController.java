package com.kairos.controller.master_data;

import com.kairos.service.master_data.TechnicalSecurityMeasureService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import static com.kairos.constant.ApiConstant.API_TECH_SEC_MEASURE_URL;

@RestController
@RequestMapping(API_TECH_SEC_MEASURE_URL)
@Api(API_TECH_SEC_MEASURE_URL)
public class TechnicalSecurityController {

    @Inject
    private TechnicalSecurityMeasureService technicalSecurityMeasureService;


    @ApiOperation("add TechnicalSecurityMeasure")
    @PostMapping("/add")
    public ResponseEntity<Object> createTechnicalSecurityMeasure(@RequestParam String securityMeasure) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.createTechnicalSecurityMeasure(securityMeasure));

    }


    @ApiOperation("get TechnicalSecurityMeasure by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getTechnicalSecurityMeasureById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getTechnicalSecurityMeasureById(id));

    }


    @ApiOperation("get all TechnicalSecurityMeasure ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllTechnicalSecurityMeasure() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.getAllTechnicalSecurityMeasure());

    }


    @ApiOperation("delete TechnicalSecurityMeasure  by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteTechnicalSecurityMeasureById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.deleteTechnicalSecurityMeasureById(id));

    }

    @ApiOperation("update TechnicalSecurityMeasure by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateTechnicalSecurityMeasure(@PathVariable BigInteger id, @RequestParam String securityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, technicalSecurityMeasureService.updateTechnicalSecurityMeasure(id, securityMeasure));

    }


}

package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingLegalBasis;
import com.kairos.service.master_data_management.processing_activity_masterdata.ProcessingLegalBasisService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import static com.kairos.constant.ApiConstant.API_PROCESSING_LEGAL_BASIS;

@RestController
@RequestMapping(API_PROCESSING_LEGAL_BASIS)
@Api(API_PROCESSING_LEGAL_BASIS)
@CrossOrigin
public class ProcessingLegalBasisController {



    @Inject
    private ProcessingLegalBasisService legalBasisService;


    @ApiOperation("add ProcessingLegalBasis")
    @PostMapping("/add")
    public ResponseEntity<Object> createProcessingLegalBasis(@RequestBody List<ProcessingLegalBasis> legalBases) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.createProcessingLegalBasis(legalBases));

    }


    @ApiOperation("get ProcessingLegalBasis by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProcessingLegalBasis(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasis(id));

    }


    @ApiOperation("get all ProcessingLegalBasis ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllProcessingLegalBasis() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getAllProcessingLegalBasis());

    }

    @ApiOperation("get ProcessingLegalBasis by name")
    @GetMapping("")
    public ResponseEntity<Object> getProcessingLegalBasisByName(@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasisByName(name));

    }


    @ApiOperation("delete ProcessingLegalBasis  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteProcessingLegalBasis(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.deleteProcessingLegalBasis(id));

    }


    @ApiOperation("update ProcessingLegalBasis by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateProcessingLegalBasis(@PathVariable BigInteger id, @RequestBody ProcessingLegalBasis legalBasis) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.updateProcessingLegalBasis(id, legalBasis));

    }


}

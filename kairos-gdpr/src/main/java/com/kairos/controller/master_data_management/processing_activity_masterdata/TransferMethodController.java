package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.TransferMethod;
import com.kairos.service.master_data_management.processing_activity_masterdata.TransferMethodService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_TRANSFER_METHOD;
/*
 *
 *  created by bobby 20/5/2018
 * */



@RestController
@RequestMapping(API_TRANSFER_METHOD)
@Api(API_TRANSFER_METHOD)
@CrossOrigin
public class TransferMethodController {

    @Inject
    private TransferMethodService transferMethodDestinationService;


    @ApiOperation("add transfer Method ")
    @PostMapping("/add")
    public ResponseEntity<Object> createTransferMethod(@RequestBody List<TransferMethod> transferMethods) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.createTransferMethod(transferMethods));

    }


    @ApiOperation("get transfer Method by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransferMethod(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethod(id));

    }


    @ApiOperation("get all transfer Method")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllTransferMethod() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getAllTransferMethod());

    }

    @ApiOperation("get transfer Method by name")
    @GetMapping("/")
    public ResponseEntity<Object> getResponsibilityTypeByName(@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethodByName(name));

    }





    @ApiOperation("delete transfer Method by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteTransferMethod(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.deleteTransferMethod(id));

    }

    @ApiOperation("update transfer Method by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateTransferMethod(@PathVariable BigInteger id, @RequestBody TransferMethod transferMethod) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.updateTransferMethod(id, transferMethod));

    }


}

package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.TransferMethod;
import com.kairos.service.master_data_management.processing_activity_masterdata.TransferMethodService;
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

import static com.kairos.constant.ApiConstant.API_TRANSFER_METHOD;
/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_TRANSFER_METHOD)
@Api(API_TRANSFER_METHOD)
public class TransferMethodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMethodController.class);

    @Inject
    private TransferMethodService transferMethodDestinationService;


    @ApiOperation("add transfer Method ")
    @PostMapping("/add")
    public ResponseEntity<Object> createTransferMethod(@PathVariable Long countryId, @RequestBody List<TransferMethod> transferMethods) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.createTransferMethod(countryId, transferMethods));

    }


    @ApiOperation("get transfer Method by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransferMethod(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethod(countryId, id));

    }


    @ApiOperation("get all transfer Method")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllTransferMethod() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getAllTransferMethod());

    }

    @ApiOperation("get transfer Method by name")
    @GetMapping("/")
    public ResponseEntity<Object> getResponsibilityTypeByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethodByName(countryId, name));

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

package com.kairos.controller.master_data;


import com.kairos.service.master_data.TransferMethodService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_TRANSFER_METHOD;

@RestController
@RequestMapping(API_TRANSFER_METHOD)
@Api(API_TRANSFER_METHOD)
public class TransferMethodController {

    @Inject
    private TransferMethodService transferMethodService;


    @ApiOperation("add transfer Method ")
    @PostMapping("/add_method")
    public ResponseEntity<Object> createTransferMethod(@RequestParam String  transferMethod) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodService.createTransferMethod(transferMethod));

    }


    @ApiOperation("get transfer Method by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getTransferMethodById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodService.getTransferMethodById(id));

    }


    @ApiOperation("get all transfer Method")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllTransferMethod() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodService.getAllTransferMethod());

    }


    @ApiOperation("delete transfer Method by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteTransferMethodById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodService.deleteTransferMethodById(id));

    }

    @ApiOperation("update transfer Method by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateTransferMethod(@PathVariable BigInteger id, @RequestParam String transferMethod) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodService.updateTransferMethod(id, transferMethod));

    }


}

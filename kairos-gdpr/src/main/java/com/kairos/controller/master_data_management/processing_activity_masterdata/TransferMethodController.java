package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.TransferMethod;
import com.kairos.service.master_data_management.processing_activity_masterdata.TransferMethodService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_TRANSFER_METHOD;
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
    public ResponseEntity<Object> createTransferMethod(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<TransferMethod> transferMethods) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.createTransferMethod(countryId, organizationId, transferMethods.getRequestBody()));

    }


    @ApiOperation("get transfer Method by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransferMethod(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethod(countryId, organizationId, id));

    }


    @ApiOperation("get all transfer Method")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllTransferMethod(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getAllTransferMethod(countryId, organizationId));

    }

    @ApiOperation("get transfer Method by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getResponsibilityTypeByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethodByName(countryId, organizationId, name));

    }


    @ApiOperation("delete transfer Method by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteTransferMethod(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.deleteTransferMethod(countryId, organizationId, id));

    }

    @ApiOperation("update transfer Method by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateTransferMethod(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody TransferMethod transferMethod) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.updateTransferMethod(countryId, organizationId, id, transferMethod));

    }


}

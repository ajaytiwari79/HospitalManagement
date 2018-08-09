package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.dto.metadata.TransferMethodDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.TransferMethod;
import com.kairos.service.master_data.processing_activity_masterdata.TransferMethodService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;

/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class TransferMethodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMethodController.class);

    @Inject
    private TransferMethodService transferMethodDestinationService;


    @ApiOperation("add transfer Method ")
    @PostMapping("/transfer_method/add")
    public ResponseEntity<Object> createTransferMethod(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<TransferMethodDTO> transferMethods) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.createTransferMethod(countryId, transferMethods.getRequestBody()));

    }


    @ApiOperation("get transfer Method by id")
    @GetMapping("/transfer_method/{id}")
    public ResponseEntity<Object> getTransferMethod(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethod(countryId, id));

    }


    @ApiOperation("get all transfer Method")
    @GetMapping("/transfer_method/all")
    public ResponseEntity<Object> getAllTransferMethod(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getAllTransferMethod(countryId));

    }

    @ApiOperation("get transfer Method by name")
    @GetMapping("/transfer_method/name")
    public ResponseEntity<Object> getResponsibilityTypeByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethodByName(countryId, name));

    }


    @ApiOperation("delete transfer Method by id")
    @DeleteMapping("/transfer_method/delete/{id}")
    public ResponseEntity<Object> deleteTransferMethod(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.deleteTransferMethod(countryId, id));

    }



    @ApiOperation("update transfer Method by id")
    @PutMapping("/transfer_method/update/{id}")
    public ResponseEntity<Object> updateTransferMethod(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody TransferMethodDTO transferMethod) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.updateTransferMethod(countryId, id, transferMethod));

    }
}

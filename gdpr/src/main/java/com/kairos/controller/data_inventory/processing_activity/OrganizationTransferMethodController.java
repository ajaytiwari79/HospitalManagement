package com.kairos.controller.data_inventory.processing_activity;

import com.kairos.gdpr.metadata.TransferMethodDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationTransferMethodService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationTransferMethodController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationTransferMethodController.class);

    @Inject
    private OrganizationTransferMethodService transferMethodDestinationService;


    @ApiOperation("add transfer Method ")
    @PostMapping("/transfer_method/add")
    public ResponseEntity<Object> createTransferMethod(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<TransferMethodDTO> transferMethods) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.createTransferMethod(unitId, transferMethods.getRequestBody()));

    }


    @ApiOperation("get transfer Method by id")
    @GetMapping("/transfer_method/{id}")
    public ResponseEntity<Object> getTransferMethod(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethod(unitId, id));

    }


    @ApiOperation("get all transfer Method")
    @GetMapping("/transfer_method/all")
    public ResponseEntity<Object> getAllTransferMethod(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getAllTransferMethod(unitId));

    }


    @ApiOperation("delete transfer Method by id")
    @DeleteMapping("/transfer_method/delete/{id}")
    public ResponseEntity<Object> deleteTransferMethod(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.deleteTransferMethod(unitId, id));

    }

    @ApiOperation("update transfer Method by id")
    @PutMapping("/transfer_method/update/{id}")
    public ResponseEntity<Object> updateTransferMethod(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody TransferMethodDTO transferMethod) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.updateTransferMethod(unitId, id, transferMethod));

    }


    @ApiOperation("save responsibility Type And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/transfer_method/suggest")
    public ResponseEntity<Object> saveTransferMethodAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<TransferMethodDTO> transferMethodDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.saveAndSuggestTransferMethods(countryId, unitId, transferMethodDTOs.getRequestBody()));

    }
    
}

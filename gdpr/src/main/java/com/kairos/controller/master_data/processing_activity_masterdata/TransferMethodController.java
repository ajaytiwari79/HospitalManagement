package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.enums.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.TransferMethodDTO;
import com.kairos.service.master_data.processing_activity_masterdata.TransferMethodService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

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
    @PostMapping("/transfer_method")
    public ResponseEntity<Object> createTransferMethod(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<TransferMethodDTO> transferMethods) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.createTransferMethod(countryId, transferMethods.getRequestBody()));
    }


    @ApiOperation("get transfer Method by id")
    @GetMapping("/transfer_method/{transferMethodId}")
    public ResponseEntity<Object> getTransferMethod(@PathVariable Long countryId, @PathVariable BigInteger transferMethodId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethod(countryId, transferMethodId));
    }


    @ApiOperation("get all transfer Method")
    @GetMapping("/transfer_method")
    public ResponseEntity<Object> getAllTransferMethod(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getAllTransferMethod(countryId));
    }

    @ApiOperation("delete transfer Method by id")
    @DeleteMapping("/transfer_method/{transferMethodId}")
    public ResponseEntity<Object> deleteTransferMethod(@PathVariable Long countryId, @PathVariable BigInteger transferMethodId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.deleteTransferMethod(countryId, transferMethodId));
    }


    @ApiOperation("update transfer Method by id")
    @PutMapping("/transfer_method/{transferMethodId}")
    public ResponseEntity<Object> updateTransferMethod(@PathVariable Long countryId, @PathVariable BigInteger transferMethodId, @Valid @RequestBody TransferMethodDTO transferMethod) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.updateTransferMethod(countryId, transferMethodId, transferMethod));
    }


    @ApiOperation("update Suggested status of Transfer methods")
    @PutMapping("/transfer_method")
    public ResponseEntity<Object> updateSuggestedStatusOfTransferMethods(@PathVariable Long countryId, @RequestBody Set<BigInteger> transferMethodIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(transferMethodIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Id Array is Empty");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.updateSuggestedStatusOfTransferMethodList(countryId, transferMethodIds, suggestedDataStatus));
    }


}

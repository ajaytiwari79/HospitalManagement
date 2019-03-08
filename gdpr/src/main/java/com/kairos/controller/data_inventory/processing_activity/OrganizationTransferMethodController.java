package com.kairos.controller.data_inventory.processing_activity;

import com.kairos.dto.gdpr.metadata.TransferMethodDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationTransferMethodService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class OrganizationTransferMethodController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationTransferMethodController.class);

    @Inject
    private OrganizationTransferMethodService transferMethodDestinationService;


    @ApiOperation("add transfer Method ")
    @PostMapping("/transfer_method")
    public ResponseEntity<Object> createTransferMethod(@PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<TransferMethodDTO> transferMethods) {
        if (CollectionUtils.isEmpty(transferMethods.getRequestBody())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.createTransferMethod(organizationId, transferMethods.getRequestBody()));

    }


    @ApiOperation("get transfer Method by id")
    @GetMapping("/transfer_method/{transferMethodId}")
    public ResponseEntity<Object> getTransferMethod(@PathVariable Long organizationId, @PathVariable Long transferMethodId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getTransferMethod(organizationId, transferMethodId));

    }


    @ApiOperation("get all transfer Method")
    @GetMapping("/transfer_method")
    public ResponseEntity<Object> getAllTransferMethod(@PathVariable Long organizationId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.getAllTransferMethod(organizationId));

    }


    @ApiOperation("delete transfer Method by id")
    @DeleteMapping("/transfer_method/{transferMethodId}")
    public ResponseEntity<Object> deleteTransferMethod(@PathVariable Long organizationId, @PathVariable Long transferMethodId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.deleteTransferMethod(organizationId, transferMethodId));

    }

    @ApiOperation("update transfer Method by id")
    @PutMapping("/transfer_method/{transferMethodId}")
    public ResponseEntity<Object> updateTransferMethod(@PathVariable Long organizationId, @PathVariable Long transferMethodId, @Valid @RequestBody TransferMethodDTO transferMethod) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.updateTransferMethod(organizationId, transferMethodId, transferMethod));

    }


    @ApiOperation("save responsibility Type And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/transfer_method/suggest")
    public ResponseEntity<Object> saveTransferMethodAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<TransferMethodDTO> transferMethodDTOs) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, transferMethodDestinationService.saveAndSuggestTransferMethods(countryId, organizationId, transferMethodDTOs.getRequestBody()));

    }

}

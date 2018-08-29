package com.kairos.controller.organization;

import com.kairos.user.organization.PaymentSettingsDTO;
import com.kairos.persistence.model.user.region.LocalAreaTag;
import com.kairos.service.organization_meta_data.OrganizationMetadataService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;


/**
 * Created by neuron on 12/6/17.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class OrganizationMetadataController {

    @Inject
    private OrganizationMetadataService organizationMetadataService;

    private static final String localAreaTagUrl = "/localAreaTags";
    private static final String organizationPaymentSettingsUrl = "/organization_payment_settings";
    private static final String organizationSickSettingsUrl = "/sick_settings";

    @ApiOperation(value = "Get Local Area Tag for a unit")
    @RequestMapping(value = localAreaTagUrl, method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getLocalAreaTags(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationMetadataService.findAllLocalAreaTags(unitId));
    }


    @ApiOperation(value = "Create Local Area Tag for a unit")
    @RequestMapping(value = localAreaTagUrl, method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createLocalAreaTag(@Validated @RequestBody LocalAreaTag localAreaTag, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationMetadataService.createNew(localAreaTag, unitId));
    }


    @ApiOperation(value = "Update Local Area Tag for a unit")
    @RequestMapping(value = localAreaTagUrl, method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateLocalAreaTag(@Validated @RequestBody LocalAreaTag localAreaTag, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationMetadataService.updateTagData(localAreaTag, unitId));
    }

   /* @ApiOperation(value = "Update Local Area Tag Busiest Time window")
    @RequestMapping(value = localAreaTagUrl+"/busiest_time_window", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateLocalAreaTagBusiestTimeWindow(@RequestParam Long localAreaTagId, @RequestBody List<DayTimeWindowDTO> dayTimeWindowDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationMetadataService.updateBusiestTimeWindow(localAreaTagId, dayTimeWindowDTOS));
    }*/


    @ApiOperation(value = "Delete Local Area Tag for a unit")
    @RequestMapping(value = localAreaTagUrl + "/{localAreaTagId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteLocalAreaTag(@Validated @PathVariable Long localAreaTagId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationMetadataService.deleteTagData(localAreaTagId));
    }

    @ApiOperation(value = "Get Payments settings for a unit")
    @RequestMapping(value = organizationPaymentSettingsUrl, method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPaymentsSettings(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationMetadataService.getPaymentSettings(unitId));
    }


    @ApiOperation(value = "Update Payments settings for a unit")
    @RequestMapping(value = organizationPaymentSettingsUrl, method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePaymentsSettings(@Validated @RequestBody PaymentSettingsDTO paymentSettingsDTO, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationMetadataService.updatePaymentsSettings(paymentSettingsDTO, unitId));
    }



}
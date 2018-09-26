package com.kairos.controller.country;

import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.service.country.ReasonCodeService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

/**
 * Created by pavan on 23/3/18.
 */
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
@RestController
public class CountryReasonCodeController {
    @Inject
    ReasonCodeService reasonCodeService;


    //Reason Code

    @ApiOperation(value = "Add ReasonCode by countryId")
    @RequestMapping(value = COUNTRY_URL + "/reason_code", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addReasonCode(@PathVariable long countryId, @Validated @RequestBody ReasonCodeDTO reasonCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.createReasonCode(countryId, reasonCodeDTO));
    }

    @ApiOperation(value = "Get ReasonCodes by countryId")
    @RequestMapping(value = COUNTRY_URL + "/reason_codes", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getReasonCodes(@RequestParam("reasonCodeType") ReasonCodeType reasonCodeType ,@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.getReasonCodes(countryId,reasonCodeType));

    }

    @ApiOperation(value = "Update ReasonCode")
    @RequestMapping(value = COUNTRY_URL + "/reason_code/{reasonCodeId}", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateReasonCode(@PathVariable long countryId,@Validated @RequestBody ReasonCodeDTO reasonCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.updateReasonCode(countryId,reasonCodeDTO));
    }

    @ApiOperation(value = "Delete ReasonCode by reasonCodeId")
    @RequestMapping(value = COUNTRY_URL + "/reason_code/{reasonCodeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteReasonCode(@PathVariable long countryId, @PathVariable long reasonCodeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.deleteReasonCode(countryId,reasonCodeId));
    }
}

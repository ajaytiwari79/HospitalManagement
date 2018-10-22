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
import static com.kairos.constants.ApiConstants.UNIT_URL;

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
    public ResponseEntity<Map<String, Object>> addReasonCodeForCountry(@PathVariable long countryId, @Validated @RequestBody ReasonCodeDTO reasonCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.createReasonCodeForCountry(countryId, reasonCodeDTO));
    }

    @ApiOperation(value = "Get ReasonCodes by countryId")
    @RequestMapping(value = COUNTRY_URL + "/reason_codes", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getReasonCodesForCountry(@RequestParam("reasonCodeType") ReasonCodeType reasonCodeType ,@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.getReasonCodesForCountry(countryId,reasonCodeType));

    }

    @ApiOperation(value = "Update ReasonCode")
    @RequestMapping(value = COUNTRY_URL + "/reason_code/{reasonCodeId}", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateReasonCodeForCountry(@PathVariable long countryId,@Validated @RequestBody ReasonCodeDTO reasonCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.updateReasonCodeForCountry(countryId,reasonCodeDTO));
    }

    @ApiOperation(value = "Delete ReasonCode by reasonCodeId")
    @RequestMapping(value = COUNTRY_URL + "/reason_code/{reasonCodeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteReasonCodeForCountry(@PathVariable long countryId, @PathVariable long reasonCodeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.deleteReasonCodeForCountry(countryId,reasonCodeId));
    }
//
    @ApiOperation(value = "Add ReasonCode by unitId")
    @RequestMapping(value = UNIT_URL + "/reason_code", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addReasonCodeForUnit(@PathVariable long unitId, @Validated @RequestBody ReasonCodeDTO reasonCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.createReasonCodeForUnit(unitId, reasonCodeDTO));
    }


    @ApiOperation(value = "Update ReasonCode ")
    @RequestMapping(value = UNIT_URL + "/reason_code/{reasonCodeId}", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateReasonCodeForUnit(@PathVariable long unitId,@Validated @RequestBody ReasonCodeDTO reasonCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.updateReasonCodeForUnit(unitId,reasonCodeDTO));
    }

    @ApiOperation(value = "Delete ReasonCode by unitId")
    @RequestMapping(value = UNIT_URL + "/reason_code/{reasonCodeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteReasonCodeForUnit(@PathVariable long unitId, @PathVariable long reasonCodeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.deleteReasonCodeForUnit(unitId,reasonCodeId));
    }

    @ApiOperation(value = "Get ReasonCodes by UnitId")
    @RequestMapping(value = UNIT_URL + "/reason_codes", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getReasonCodesByUnitId(@RequestParam("reasonCodeType") ReasonCodeType reasonCodeType ,@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.getReasonCodesByUnitId(unitId,reasonCodeType));

    }


}

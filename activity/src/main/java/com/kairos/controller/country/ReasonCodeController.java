package com.kairos.controller.country;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.service.reason_code.ReasonCodeService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created by pavan on 23/3/18.
 */
@RequestMapping(API_V1)
@Api(API_V1)
@RestController
public class ReasonCodeController {
    @Inject
    private ReasonCodeService reasonCodeService;

    @ApiOperation(value = "Add ReasonCode by countryId")
    @RequestMapping(value = COUNTRY_URL + "/reason_code", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addReasonCodeForCountry(@PathVariable long countryId, @Validated @RequestBody ReasonCodeDTO reasonCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.createReasonCodeForCountry(countryId, reasonCodeDTO));
    }

    @ApiOperation(value = "Get ReasonCodes by countryId")
    @RequestMapping(value = COUNTRY_URL + "/reason_codes", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getReasonCodesForCountry(@RequestParam("reasonCodeType") ReasonCodeType reasonCodeType, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.getReasonCodesForCountry(countryId, reasonCodeType));

    }

    @ApiOperation(value = "Update ReasonCode")
    @RequestMapping(value = COUNTRY_URL + "/reason_code/{reasonCodeId}", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateReasonCodeForCountry(@PathVariable long countryId, @Validated @RequestBody ReasonCodeDTO reasonCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.updateReasonCodeForCountry(countryId, reasonCodeDTO));
    }

    @ApiOperation(value = "Delete ReasonCode by reasonCodeId")
    @RequestMapping(value = COUNTRY_URL + "/reason_code/{reasonCodeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteReasonCodeForCountry(@PathVariable long countryId, @PathVariable BigInteger reasonCodeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.deleteReasonCode(reasonCodeId));
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
    public ResponseEntity<Map<String, Object>> updateReasonCodeForUnit(@PathVariable long unitId, @Validated @RequestBody ReasonCodeDTO reasonCodeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.updateReasonCodeForUnit(unitId, reasonCodeDTO));
    }

    @ApiOperation(value = "Delete ReasonCode by unitId")
    @RequestMapping(value = UNIT_URL + "/reason_code/{reasonCodeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteReasonCodeForUnit(@PathVariable long unitId, @PathVariable BigInteger reasonCodeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.deleteReasonCode(reasonCodeId));
    }

    @ApiOperation(value = "Get ReasonCodes by UnitId")
    @GetMapping(value = UNIT_URL + "/reason_codes")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getReasonCodesByUnitId(@RequestParam("reasonCodeType") ReasonCodeType reasonCodeType, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.getReasonCodesByUnitId(unitId, reasonCodeType));

    }

    @ApiOperation(value = "Check if any reason code exists with the provided time type Id")
    @GetMapping(value = COUNTRY_URL + "/reason_codes/link_with_time_type/{timeTypeId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> anyReasonCodeLinkedWithTimeType(@PathVariable BigInteger timeTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.anyReasonCodeLinkedWithTimeType(timeTypeId));

    }



    @PutMapping(value = UNIT_URL + "/reason_code/{id}/language_settings")
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfReasonCodesOfUnit(@PathVariable BigInteger id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.updateTranslation(id,translations));
    }

    @PutMapping(value = COUNTRY_URL + "/reason_code/{id}/language_settings")
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfReasonCodesOfCountry(@PathVariable BigInteger id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, reasonCodeService.updateTranslation(id,translations));
    }

    @PostMapping(value = "transfer_reason_code")
    @ApiOperation("Add reason code data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> transferReasonCode(@RequestBody List<ReasonCodeDTO> reasonCodeDTOS) {
        reasonCodeService.transferReasonCode(reasonCodeDTOS);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }
}

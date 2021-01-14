package com.kairos.controller.day_type;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.*;

@RequestMapping(API_V1)
@Api(API_V1)
@RestController
public class DayTypeController {

    @Inject
    private DayTypeService dayTypeService;

    @ApiOperation(value = "Add DayType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/dayType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addDayType(@PathVariable long countryId, @Validated @RequestBody DayTypeDTO dayTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.createDayType(dayTypeDTO, countryId));
    }

    @ApiOperation(value = "Get DayType by countryId")
    @GetMapping(value = COUNTRY_URL + "/dayType")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getAllDayTypeByCountryId(countryId));
    }

    @ApiOperation(value = "Get DayType for unit")
    @GetMapping(value = UNIT_URL + "/dayType")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayTypeForUnit() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getAllDayTypeByCountryId(UserContext.getUserDetails().getCountryId()));
    }

    @ApiOperation(value = "Update DayType")
    @RequestMapping(value = COUNTRY_URL + "/dayType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateDayType(@Validated @RequestBody DayTypeDTO dayTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.updateDayType(dayTypeDTO));
    }

    @ApiOperation(value = "Delete DayType by dayTypeId")
    @RequestMapping(value = COUNTRY_URL + "/dayType/{dayTypeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteDayType(@PathVariable BigInteger dayTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.deleteDayType(dayTypeId));

    }

    @ApiOperation(value = "Delete DayType by dayTypeId")
    @GetMapping(value = COUNTRY_URL + "/active_dayTypes")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteDayType(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getCurrentApplicableDayType(countryId));

    }

    @PutMapping(value = COUNTRY_URL + "/day_type/{id}/language_settings")
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfDayType(@PathVariable BigInteger id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.updateTranslation(id,translations));
    }

    @ApiOperation(value = "Get DayType by countryId")
    @PutMapping(value =  "/dayType_byIds")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayType(@RequestBody Set<BigInteger> dayTypeIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getDayTypeWithCountryHolidayCalender(dayTypeIds));
    }

    @ApiOperation(value = "Get DayType and Presence Type")
    @GetMapping(UNIT_URL + "/getWtaTemplateDefaultDataInfoByUnitId")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getWtaTemplateDefaultDataInfo(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getWtaTemplateDefaultDataInfo(unitId,false));

    }

    @ApiOperation(value = "Get DayType and Presence Type")
    @GetMapping(value = COUNTRY_URL + "/getWtaTemplateDefaultDataInfo")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getWtaTemplateDefaultDataInfoByCountryId(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getWtaTemplateDefaultDataInfo(countryId,true));

    }

    @ApiOperation(value = "get DayTypes And Public Holiday")
    @GetMapping(UNIT_URL + "/get_dayTypes_and_public_holiday")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayTypesAndPublicHoliday(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getDayTypesAndPublicHoliday(UserContext.getUserDetails().getCountryId()));

    }

}

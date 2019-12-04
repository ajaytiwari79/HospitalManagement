package com.kairos.controller.shift;

import com.kairos.dto.activity.shift.BlockSettingDTO;
import com.kairos.service.shift.BlockSettingService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.ApiConstants.API_UNIT_URL;

/**
 * Created By G.P.Ranjan on 3/12/19
 **/
@RestController
@RequestMapping(API_UNIT_URL)
@Api(API_UNIT_URL)
public class BlockSettingController {
    @Inject
    private BlockSettingService blockSettingService;

    @ApiOperation("create or update block setting details")
    @PutMapping(value = "/block_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveBlockSettingDetails(@PathVariable Long unitId, @RequestBody BlockSettingDTO blockSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, blockSettingService.saveBlockSettingDetails(unitId, blockSettingDTO));
    }

    @ApiOperation("get block setting details")
    @GetMapping(value = "/block_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getBlockSettingDetails(@PathVariable Long unitId, @RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam(value = "endDate",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, blockSettingService.getBlockSettingDetails(unitId, startDate, isNull(endDate) ? startDate : endDate));
    }

    @ApiOperation("delete block setting detail")
    @DeleteMapping(value = "/block_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteBlockSettingDetail(@PathVariable Long unitId, @RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, blockSettingService.deleteBlockSettingDetail(unitId, date));
    }
}

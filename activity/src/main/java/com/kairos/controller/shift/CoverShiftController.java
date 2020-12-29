package com.kairos.controller.shift;

import com.kairos.dto.activity.shift.ShiftActivitiesIdDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.shift.CoverShiftSetting;
import com.kairos.service.shift.CoverShiftService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL)
@Api(API_UNIT_URL)
public class CoverShiftController {

    @Inject private CoverShiftService coverShiftService;

    @ApiOperation("get eligible staffs")
    @PostMapping(value = "/get_eligible_staffs")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEligibleStaffs(@PathVariable Long unitId, @RequestParam BigInteger shiftId, @RequestBody CoverShiftSetting coverShiftSetting) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, coverShiftService.getEligibleStaffs(shiftId,coverShiftSetting).stream().map(staff -> staff.getFirstName()+" "+staff.getLastName()).collect(Collectors.toSet()));
    }
}

package com.kairos.controller.shift;

import com.kairos.enums.shift.ViewType;
import com.kairos.service.shift.ActivityCardInformation;
import com.kairos.service.shift.ActivityCardInformationService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL)
@Api(API_UNIT_URL)
public class ActivityCardInformationController {

    @Inject
    private ActivityCardInformationService activityCardInformationService;

    @ApiOperation("create or update Activity Card Information")
    @PutMapping("/activity_card_information/{staffId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateActivityCardInformation(@PathVariable Long unitId,@PathVariable(required = false) Long staffId, @RequestBody @Valid ActivityCardInformation activityCardInformation) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityCardInformationService.updateActivityCardInformation(activityCardInformation));
    }

    @ApiOperation("get Activity Card Information")
    @GetMapping("/activity_card_information/{staffId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityCardInformation(@PathVariable Long unitId, @PathVariable Long staffId, @RequestParam ViewType viewType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityCardInformationService.getActivityCardInformation(unitId, staffId,viewType));
    }
}

package com.kairos.activity.controller.activity;

import com.kairos.activity.service.activity.ActivityCategoryService;
import com.kairos.activity.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.API_ORGANIZATION_URL;

/**
 * Created by pavan on 16/2/18.
 */

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class ActivityCategoryController {

    @Inject private ActivityCategoryService activityCategoryService;

//    @ApiOperation("Update Activity Category")
//    @PutMapping(value = "/country/{countryId}/activityCategory/{activityCategoryId}")
//    ResponseEntity<Map<String, Object>> updateActivityCategory(@PathVariable Long countryId, @PathVariable BigInteger activityCategoryId, @RequestParam("name") String name) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityCategoryService.updateActivityCategory(countryId, activityCategoryId,name));
//    }

    @ApiOperation("Delete Activity Category")
    @DeleteMapping(value = "/country/{countryId}/activityCategory/{activityCategoryId}")
    ResponseEntity<Map<String, Object>> deleteActivityCategory(@PathVariable Long countryId, @PathVariable BigInteger activityCategoryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityCategoryService.deleteActivityCategory(countryId, activityCategoryId));
    }

//    @ApiOperation("Update Activity Category at unit")
//    @PutMapping(value = "/unit/{unitId}/activityCategory/{activityCategoryId}")
//    ResponseEntity<Map<String, Object>> updateActivityCategoryByUnit(@PathVariable Long unitId, @PathVariable BigInteger activityCategoryId, @RequestParam("name") String name) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityCategoryService.updateActivityCategoryByUnit(unitId, activityCategoryId,name));
//    }

    @ApiOperation("Delete Activity Category at Unit Level")
    @DeleteMapping(value = "/unit/{unitId}/activityCategory/{activityCategoryId}")
    ResponseEntity<Map<String, Object>> deleteActivityCategoryByUnit(@PathVariable Long unitId, @PathVariable BigInteger activityCategoryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityCategoryService.deleteActivityCategoryByUnit(unitId, activityCategoryId));
    }
}

package com.kairos.controller.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityRankingDTO;
import com.kairos.service.unit_settings.ActivityRankingService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

@RestController
@RequestMapping(API_V1)
@Api(value = API_V1)
public class ActivityRankingController {

    @Inject
    private ActivityRankingService activityRankingService;

    @ApiOperation(value = "save a absence_ranking ")
    @PostMapping(value =  "/activity_ranking")
    public ResponseEntity<Map<String, Object>> saveAbsenceRanking(@RequestBody @Valid ActivityRankingDTO activityRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.saveActivityRanking(activityRankingDTO));
    }

    @ApiOperation(value = "get  absence_ranking settings of expertise")
    @GetMapping(value =   "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> getAbsenceRanking(@PathVariable Long expertiseId, @RequestParam(value = "published", required = false) Boolean published) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.getAbsenceRankingSettings(expertiseId, published));
    }

    @ApiOperation(value = "get  presence_ranking settings ")
    @GetMapping(value =   "/unit/{unitId}/presence_ranking")
    public ResponseEntity<Map<String, Object>> getPresenceRanking(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.getPresenceRankingSettings(unitId));
    }

    @ApiOperation(value = "update a activity_ranking settings")
    @PutMapping(value = "/activity_ranking")
    public ResponseEntity<Map<String, Object>> updateAbsenceRanking( @RequestBody @Valid ActivityRankingDTO activityRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.updateActivityRankingSettings(activityRankingDTO));
    }




    @ApiOperation(value = "published a absence_ranking settings")
    @PutMapping(value =  "/activity_ranking/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishAbsenceRanking(@PathVariable BigInteger id, @RequestParam("publishedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.publishActivityRanking(id, publishedDate));
    }

    @ApiOperation(value = "delete activity_ranking")
    @DeleteMapping(value = "/activity_ranking/{id}")
    public ResponseEntity<Map<String, Object>> deleteSeniorDays(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.deleteActivityRankingSettings(id));
    }

    @ApiOperation(value = "get  activities")
    @GetMapping(value = "/activity_ranking/activities")
    public ResponseEntity<Map<String, Object>> getAllAbsenceActivities(@RequestParam(value = "unitId",required = false) Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,unitId==null? activityRankingService.findAllAbsenceActivities(): activityRankingService.findAllPresenceActivities(unitId));
    }

    //use for created activity to add in unit ranking list
    @ApiOperation(value = "create a presence ranking ")
    @PostMapping(value =  "/unit/{unitId}/create_activity_ranking")
    public ResponseEntity<Map<String, Object>> createPresenceRanking(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.createPresenceRanking(unitId));
    }

    //use for created activity to add in unit ranking list
    @ApiOperation(value = "create a absence ranking ")
    @PostMapping(value =  "/country/{countryId}/create_activity_ranking")
    public ResponseEntity<Map<String, Object>> createAbsenceRanking(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.createAbsenceRanking(countryId));
    }

    @ApiOperation(value = "get  all absence ranking")
    @GetMapping(value = COUNTRY_URL + "/absence_activity_ranking")
    public ResponseEntity<Map<String, Object>> getAllAbsenceActivitiesRanking(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.getAllAbsenceActivitiesRanking(countryId));
    }

}
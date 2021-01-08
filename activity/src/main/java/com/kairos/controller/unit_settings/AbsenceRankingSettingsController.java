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

@RestController
@RequestMapping(API_V1)
@Api(value = API_V1)
public class AbsenceRankingSettingsController {

    @Inject
    private ActivityRankingService activityRankingService;

    @ApiOperation(value = "save a absence_ranking  for expertise")
    @PostMapping(value =  "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> saveAbsenceRanking(@PathVariable Long expertiseId, @RequestBody @Valid ActivityRankingDTO activityRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.saveActivityRanking(activityRankingDTO));
    }

    @ApiOperation(value = "get  absence_ranking settings of expertise")
    @GetMapping(value =   "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> getAbsenceRanking(@PathVariable Long expertiseId, @RequestParam(value = "published", required = false) Boolean published) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.getActivityRanking(expertiseId, published));
    }

    @ApiOperation(value = "update a absence_ranking settings for expertise")
    @PutMapping(value = "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> updateAbsenceRanking( @RequestBody @Valid ActivityRankingDTO activityRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.updateActivityRanking(activityRankingDTO));
    }




    @ApiOperation(value = "published a absence_ranking settings for expertise")
    @PutMapping(value =  "/expertise/{expertiseId}/absence_ranking/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishAbsenceRanking(@PathVariable BigInteger id, @RequestParam("publishedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.publishActivityRanking(id, publishedDate));
    }

    @ApiOperation(value = "delete a senior days for expertise")
    @DeleteMapping(value = "/absence_ranking/{id}")
    public ResponseEntity<Map<String, Object>> deleteSeniorDays(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.deleteActivityRanking(id));
    }

    @ApiOperation(value = "get absence activities")
    @GetMapping(value = "/absence_ranking/activities")
    public ResponseEntity<Map<String, Object>> getAllAbsenceActivities() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityRankingService.findAllAbsenceActivities());
    }

}

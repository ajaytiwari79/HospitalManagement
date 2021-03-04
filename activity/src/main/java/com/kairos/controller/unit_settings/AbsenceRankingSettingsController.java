package com.kairos.controller.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.AbsenceRankingDTO;
import com.kairos.service.unit_settings.AbsenceRankingSettingsService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

@RestController
@RequestMapping(API_V1)
@Api(value = API_V1)
public class AbsenceRankingSettingsController {

    @Inject
    private AbsenceRankingSettingsService absenceRankingSettingsService;

    @ApiOperation(value = "save a absence_ranking ")
    @PostMapping(value =  "/activity_ranking")
    public ResponseEntity<Map<String, Object>> saveAbsenceRanking(@RequestBody @Valid AbsenceRankingDTO absenceRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.saveAbsenceRankingSettings(absenceRankingDTO));
    }

    @ApiOperation(value = "get  absence_ranking settings of expertise")
    @GetMapping(value =   "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> getAbsenceRanking(@PathVariable Long expertiseId, @RequestParam(value = "published", required = false) Boolean published) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.getAbsenceRankingSettings(expertiseId, published));
    }

    @ApiOperation(value = "get  presence_ranking settings ")
    @GetMapping(value =   "/unit/{unitId}/presence_ranking")
    public ResponseEntity<Map<String, Object>> getPresenceRanking(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.getPresenceRankingSettings(unitId));
    }

    @ApiOperation(value = "update a activity_ranking settings")
    @PutMapping(value = "/activity_ranking")
    public ResponseEntity<Map<String, Object>> updateAbsenceRanking( @RequestBody @Valid AbsenceRankingDTO absenceRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.updateAbsenceRankingSettings(absenceRankingDTO));
    }




    @ApiOperation(value = "published a absence_ranking settings")
    @PutMapping(value =  "/activity_ranking/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishAbsenceRanking(@PathVariable BigInteger id, @RequestParam("publishedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.publishAbsenceRanking(id, publishedDate));
    }

    @ApiOperation(value = "delete activity_ranking")
    @DeleteMapping(value = "/activity_ranking/{id}")
    public ResponseEntity<Map<String, Object>> deleteSeniorDays(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.deleteAbsenceRankingSettings(id));
    }

    @ApiOperation(value = "get  activities")
    @GetMapping(value = "/activity_ranking/activities")
    public ResponseEntity<Map<String, Object>> getAllAbsenceActivities(@RequestParam(value = "unitId",required = false) Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,unitId==null? absenceRankingSettingsService.findAllAbsenceActivities():absenceRankingSettingsService.findAllPresenceActivities(unitId));
    }

}

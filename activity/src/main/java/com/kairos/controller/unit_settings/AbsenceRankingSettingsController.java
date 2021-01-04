package com.kairos.controller.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.AbsenceRankingDTO;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.experties.CareDaysDetails;
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
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

@RestController
@RequestMapping(API_V1)
@Api(value = API_V1)
public class AbsenceRankingSettingsController {

    @Inject
    private AbsenceRankingSettingsService absenceRankingSettingsService;

    @ApiOperation(value = "save a absence_ranking  for expertise")
    @PostMapping(value =  "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> saveAbsenceRanking(@PathVariable Long expertiseId, @RequestBody @Valid AbsenceRankingDTO absenceRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.saveAbsenceRankingSettings(absenceRankingDTO));
    }

    @ApiOperation(value = "get  absence_ranking settings of expertise")
    @GetMapping(value =   "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> getAbsenceRanking(@PathVariable Long expertiseId, @RequestParam(value = "published", required = false) Boolean published) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.getAbsenceRankingSettings(expertiseId, published));
    }

    @ApiOperation(value = "update a absence_ranking settings for expertise")
    @PutMapping(value = "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> updateAbsenceRanking( @RequestBody @Valid AbsenceRankingDTO absenceRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.updateAbsenceRankingSettings(absenceRankingDTO));
    }




    @ApiOperation(value = "published a absence_ranking settings for expertise")
    @PutMapping(value =  "/expertise/{expertiseId}/absence_ranking/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishAbsenceRanking(@PathVariable BigInteger id, @RequestParam("publishedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.publishSeniorDays(id, publishedDate));
    }

    @ApiOperation(value = "delete a senior days for expertise")
    @DeleteMapping(value = "/absence_ranking/{id}")
    public ResponseEntity<Map<String, Object>> deleteSeniorDays(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.deleteAbsenceRankingSettings(id));
    }

    @ApiOperation(value = "get absence activities")
    @GetMapping(value = "/absence_ranking/activities")
    public ResponseEntity<Map<String, Object>> getAllAbsenceActivities() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.findAllAbsenceActivities());
    }

}

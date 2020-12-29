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

    @ApiOperation(value = "save a senior days for expertise")
    @PostMapping(value =  "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> saveSeniorDays(@PathVariable Long expertiseId, @RequestBody @Valid AbsenceRankingDTO absenceRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.saveAbsenceRankingSettings(absenceRankingDTO));
    }

    @ApiOperation(value = "get  senior days  settings of expertise")
    @GetMapping(value =   "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> getSeniorDays(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.getAbsenceRankingSettings(expertiseId));
    }

    @ApiOperation(value = "update a senior days settings for expertise")
    @PutMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/absence_ranking")
    public ResponseEntity<Map<String, Object>> updateSeniorDays( @RequestBody @Valid AbsenceRankingDTO absenceRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, absenceRankingSettingsService.updateAbsenceRankingSettings(absenceRankingDTO));
    }

}

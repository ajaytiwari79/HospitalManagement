package com.kairos.controller.expertise;

import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.experties.CareDaysDetails;
import com.kairos.service.expertise.SeniorDaysService;
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
public class SeniorDaysController {
    @Inject
    private SeniorDaysService seniorDaysService;

    @ApiOperation(value = "save a senior days for expertise")
    @PostMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/senior_days")
    public ResponseEntity<Map<String, Object>> saveSeniorDays(@PathVariable Long expertiseId, @RequestBody @Valid CareDaysDetails careDaysDetails) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, seniorDaysService.saveSeniorDays(expertiseId, careDaysDetails));
    }

    @ApiOperation(value = "get  senior days  settings of expertise")
    @GetMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/senior_days")
    public ResponseEntity<Map<String, Object>> getSeniorDays(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, seniorDaysService.getSeniorDays(expertiseId));
    }

    @ApiOperation(value = "update a senior days settings for expertise")
    @PutMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/senior_days")
    public ResponseEntity<Map<String, Object>> updateSeniorDays( @RequestBody @Valid CareDaysDetails careDaysDetails) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, seniorDaysService.updateSeniorDays(careDaysDetails));
    }

    @ApiOperation(value = "Add a senior days settings for senior_days")
    @PostMapping(value =  COUNTRY_URL + "/senior_days/{seniorDayId}")
    public ResponseEntity<Map<String, Object>> addMatrixInSeniorDays(@PathVariable Long seniorDayId, @RequestBody @Valid List<AgeRangeDTO> ageRangeDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, seniorDaysService.addMatrixInSeniorDays(seniorDayId,ageRangeDTOS));
    }

    @ApiOperation(value = "GET a senior_days settings for senior_days")
    @GetMapping(value =  COUNTRY_URL + "/senior_days/{seniorDayId}")
    public ResponseEntity<Map<String, Object>> getMatrixOfSeniorDays(@PathVariable Long seniorDayId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, seniorDaysService.getMatrixOfSeniorDays(seniorDayId));
    }

    @ApiOperation(value = "Add a senior days settings for senior_days")
    @PutMapping(value =  COUNTRY_URL + "/senior_days/{seniorDayId}")
    public ResponseEntity<Map<String, Object>> updateMatrixInSeniorDays(@PathVariable Long seniorDayId, @RequestBody @Valid List<AgeRangeDTO> careDaysDetails) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, seniorDaysService.updateMatrixInSeniorDays(seniorDayId,careDaysDetails));
    }

    @ApiOperation(value = "published a senior days settings for expertise")
    @PutMapping(value =  COUNTRY_URL + "/senior_days/{seniorDayId}/published")
    public ResponseEntity<Map<String, Object>> publishSeniorDays(@PathVariable Long seniorDayId, @RequestParam("publishedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, seniorDaysService.publishSeniorDays(seniorDayId, publishedDate));
    }

    @ApiOperation(value = "delete a senior days for expertise")
    @DeleteMapping(value =  COUNTRY_URL + "/senior_days/{seniorDayId}")
    public ResponseEntity<Map<String, Object>> deleteSeniorDays(@PathVariable Long seniorDayId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, seniorDaysService.deleteSeniorDays(seniorDayId));
    }
}

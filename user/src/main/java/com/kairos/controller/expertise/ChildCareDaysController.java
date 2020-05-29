package com.kairos.controller.expertise;

import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.experties.CareDaysDetails;
import com.kairos.service.expertise.ChildCareDaysService;
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
public class ChildCareDaysController {
    @Inject
    private ChildCareDaysService childCareDaysService;

    @ApiOperation(value = "save a child care days for expertise")
    @PostMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/child_care_days")
    public ResponseEntity<Map<String, Object>> saveSeniorDays(@PathVariable Long expertiseId, @RequestBody @Valid CareDaysDetails careDaysDetails) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, childCareDaysService.saveChildCareDays(expertiseId, careDaysDetails));
    }

    @ApiOperation(value = "get  child care days  settings of expertise")
    @GetMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/child_care_days")
    public ResponseEntity<Map<String, Object>> getSeniorDays(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, childCareDaysService.getChildCareDays(expertiseId));
    }

    @ApiOperation(value = "update a child care days settings for expertise")
    @PutMapping(value =  COUNTRY_URL + "/expertise/{expertiseId}/child_care_days")
    public ResponseEntity<Map<String, Object>> updateSeniorDays( @RequestBody @Valid CareDaysDetails careDaysDetails) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, childCareDaysService.updateChildCareDays(careDaysDetails));
    }

    @ApiOperation(value = "Add a child care days settings for child_care_days")
    @PostMapping(value =  COUNTRY_URL + "/child_care_days/{childCareDayId}")
    public ResponseEntity<Map<String, Object>> addMatrixInSeniorDays(@PathVariable Long childCareDayId, @RequestBody @Valid List<AgeRangeDTO> ageRangeDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, childCareDaysService.addMatrixInChildCareDays(childCareDayId,ageRangeDTOS));
    }

    @ApiOperation(value = "GET a child_care_days settings for child_care_days")
    @GetMapping(value =  COUNTRY_URL + "/child_care_days/{childCareDayId}")
    public ResponseEntity<Map<String, Object>> getMatrixOfSeniorDays(@PathVariable Long childCareDayId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, childCareDaysService.getMatrixOfChildCareDays(childCareDayId));
    }

    @ApiOperation(value = "Add a child care days settings for child_care_days")
    @PutMapping(value =  COUNTRY_URL + "/child_care_days/{childCareDayId}")
    public ResponseEntity<Map<String, Object>> updateMatrixInSeniorDays(@PathVariable Long childCareDayId, @RequestBody @Valid List<AgeRangeDTO> careDaysDetails) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, childCareDaysService.updateMatrixInChildCareDays(childCareDayId,careDaysDetails));
    }

    @ApiOperation(value = "published a child care days settings for expertise")
    @PutMapping(value =  COUNTRY_URL + "/child_care_days/{childCareDayId}/published")
    public ResponseEntity<Map<String, Object>> publishSeniorDays(@PathVariable Long childCareDayId,  @RequestParam("publishedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, childCareDaysService.publishChildCareDays(childCareDayId, publishedDate));
    }
}

package com.kairos.controller.night_worker;

import com.kairos.service.night_worker.NightWorkerService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.dto.activity.night_worker.NightWorkerGeneralResponseDTO;
import com.kairos.dto.activity.night_worker.QuestionnaireAnswerResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class NightWorkerController {

    @Inject
    NightWorkerService nightWorkerService;

    @ApiOperation(value = "update night worker general details")
    @PutMapping(value = "/staff/{staffId}/night_worker_general")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateNightWorkerGeneralDetails(@PathVariable Long staffId, @PathVariable Long unitId,
                                                           @RequestBody @Valid NightWorkerGeneralResponseDTO nightWorkerGeneralResponseDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.updateNightWorkerGeneralDetails(unitId, staffId, nightWorkerGeneralResponseDTO));
    }

    @ApiOperation(value = "get night worker general details")
    @GetMapping(value = "/staff/{staffId}/night_worker_general")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getNightWorkerGeneralDetails(@PathVariable Long staffId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.getNightWorkerDetailsOfStaff(unitId, staffId));
    }

    @ApiOperation(value = "get night worker questionnaire details")
    @GetMapping(value = "/staff/{staffId}/night_worker_questionnaire")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getNightWorkerQuestionnaireDetails(@PathVariable Long staffId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.getNightWorkerQuestionnaire(unitId, staffId));
    }

    @ApiOperation(value = "update night worker questionnaire details")
    @PutMapping(value = "/staff/{staffId}/night_worker_questionnaire/{questionnaireId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateNightWorkerQustionnaie(@PathVariable Long staffId, @PathVariable Long unitId, @PathVariable BigInteger questionnaireId,
                                                                                  @RequestBody @Valid QuestionnaireAnswerResponseDTO questionnaireAnswerResponseDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.updateNightWorkerQuestionnaire(unitId, staffId, questionnaireId, questionnaireAnswerResponseDTO));
    }

    @ApiOperation(value = "update night worker eligibility status")
    @PutMapping(value = "/night_worker/eligibility_status")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateNightWorkerEligibilityOfStaff() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.updateNightWorkerEligibilityOfStaff());
    }
}

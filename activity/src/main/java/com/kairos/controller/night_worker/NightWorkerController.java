package com.kairos.controller.night_worker;

import com.kairos.dto.activity.night_worker.NightWorkerGeneralResponseDTO;
import com.kairos.dto.activity.night_worker.QuestionnaireAnswerResponseDTO;
import com.kairos.service.night_worker.NightWorkerService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.UNIT_URL;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class NightWorkerController {

    @Inject
    NightWorkerService nightWorkerService;

    @ApiOperation(value = "update night worker general details")
    @PutMapping(value = UNIT_URL+"/staff/{staffId}/night_worker_general")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateNightWorkerGeneralDetails(@PathVariable Long staffId, @PathVariable Long unitId,
                                                           @RequestBody @Valid NightWorkerGeneralResponseDTO nightWorkerGeneralResponseDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.updateNightWorkerGeneralDetails(unitId, staffId, nightWorkerGeneralResponseDTO));
    }

    @ApiOperation(value = "get night worker general details")
    @GetMapping(value = UNIT_URL+"/staff/{staffId}/night_worker_general")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getNightWorkerGeneralDetails(@PathVariable Long staffId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.getNightWorkerDetailsOfStaff(unitId, staffId));
    }

    @ApiOperation(value = "get night worker questionnaire details")
    @GetMapping(value = UNIT_URL+"/staff/{staffId}/night_worker_questionnaire")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getNightWorkerQuestionnaireDetails(@PathVariable Long staffId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.getNightWorkerQuestionnaire(staffId));
    }

    @ApiOperation(value = "update night worker questionnaire details")
    @PutMapping(value = UNIT_URL+"/staff/{staffId}/night_worker_questionnaire/{questionnaireId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateNightWorkerQustionnaie(@PathVariable Long staffId, @PathVariable Long unitId, @PathVariable BigInteger questionnaireId,
                                                                                  @RequestBody @Valid QuestionnaireAnswerResponseDTO questionnaireAnswerResponseDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.updateNightWorkerQuestionnaire(questionnaireId, questionnaireAnswerResponseDTO));
    }

    @ApiOperation(value = "update night worker eligibility status")
    @PutMapping(value = UNIT_URL+"/night_worker/eligibility_status")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateNightWorkerEligibilityOfStaff() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, nightWorkerService.updateNightWorkerEligibilityOfStaff());
    }
    @ApiOperation(value = "update night workers")
    @PutMapping(value = "/update_night_workers")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateNightWorkers(@RequestBody List<Map> employments) {
        nightWorkerService.updateNightWorkers(employments);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    @ApiOperation(value = "get night worker details")
    @PostMapping(value = UNIT_URL+"/get_night_worker_details")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffNightWorkerDetails(@RequestBody List<Long> staffIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,nightWorkerService.getStaffNightWorkerDetails(staffIds));
    }



    @ApiOperation(value = "Register job for night worker")
    @PostMapping(value = "/register_job_for_night_worker")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> registerJobForNightWorker() {
        nightWorkerService.registerJobForNightWorker();
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }
}

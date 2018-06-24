package com.kairos.controller.master_data_management.questionnaire_template;


import com.kairos.service.master_data_management.questionnaire_template.MasterQuestionService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_MASTER_QUESTIONNAIRE_TEMPLATE;

@RestController
@RequestMapping(API_MASTER_QUESTIONNAIRE_TEMPLATE)
@Api(API_MASTER_QUESTIONNAIRE_TEMPLATE)
public class MasterQuestionController {


    @Inject
    private MasterQuestionService masterQuestionService;





    @GetMapping("/question/{id}")
    public ResponseEntity<Object> getMasterQuestion(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.getMasterQuestion(countryId,organizationId,id));
    }


    @GetMapping("/question/all")
    public ResponseEntity<Object> getAllMasterQuestion(@PathVariable Long countryId,@PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.getAllMasterQuestion(countryId,organizationId));
    }




    @DeleteMapping("/question/delete/{id}")
    public ResponseEntity<Object> deleteMasterQuestion(@PathVariable Long countryId,@PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.deleteMasterQuestion(countryId,organizationId,id));
    }

  /*  @PutMapping("/section/{id}/question")
    public ResponseEntity<Object> updateMasterQuestion(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody ValidateListOfRequestBody<MasterQuestionDTO> masterQuestionDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.updateExistingQuestionAndCreateNewQuestions(countryId, id, masterQuestionDto.getRequestBody()));
    }*/

}

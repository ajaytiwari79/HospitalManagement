package com.kairos.controller.master_data_management.questionnaire_template;


import com.kairos.dto.master_data.MasterQuestionnaireSectionDto;
import com.kairos.service.master_data_management.questionnaire_template.MasterQuestionnaireSectionService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.POST;

import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_MASTER_QUESTION_SECTION;

@RestController
@RequestMapping(API_MASTER_QUESTION_SECTION)
@Api(API_MASTER_QUESTION_SECTION)
public class MasterQuestionnaireSectionController {


    @Inject
    private MasterQuestionnaireSectionService masterQuestionnaireSectionService;


   /* @PostMapping("/add")
    public ResponseEntity<Object> createMasterQuestionnaireSection(@PathVariable Long countryId, @Valid @RequestBody ValidateListOfRequestBody<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.addMasterQuestionSection(countryId, masterQuestionnaireSectionDto));

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteMasterQuestionnaireSection(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.deleteMasterQuestionnaireSection(id));
    }


*/

}

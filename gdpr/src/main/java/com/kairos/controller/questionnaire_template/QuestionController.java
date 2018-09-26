package com.kairos.controller.questionnaire_template;


import com.kairos.service.questionnaire_template.QuestionService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;



@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class QuestionController {


    @Inject
    private QuestionService masterQuestionService;


    /**
     * @param countryId
     * @return
     */
    @ApiOperation("get All question of Questionnaire section")
    @GetMapping("/question_section/question/all")
    public ResponseEntity<Object> getAllMasterQuestion(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.getAllMasterQuestion(countryId));
    }


    @DeleteMapping(COUNTRY_URL+"/question_section/{sectionId}/question/{questionId}")
    public ResponseEntity<Object> deleteMasterQuestion(@PathVariable Long countryId, @PathVariable BigInteger questionId, @PathVariable BigInteger sectionId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.deleteMasterQuestion(countryId, questionId,sectionId));
    }

    @DeleteMapping(UNIT_URL+"/question_section/{sectionId}/question/{questionId}")
    public ResponseEntity<Object> deleteQuestionOfQuestionnaireSection(@PathVariable Long unitId, @PathVariable BigInteger questionId, @PathVariable BigInteger sectionId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.deleteQuestionOfQuestionnaireSectionOfUnit(unitId, questionId,sectionId));
    }


}

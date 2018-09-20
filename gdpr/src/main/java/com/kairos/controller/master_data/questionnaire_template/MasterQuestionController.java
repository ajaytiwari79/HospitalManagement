package com.kairos.controller.master_data.questionnaire_template;


import com.kairos.service.master_data.questionnaire_template.MasterQuestionService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class MasterQuestionController {


    @Inject
    private MasterQuestionService masterQuestionService;


    /**
     * @param countryId
     * @param id             id of question
     * @return return MasterQuestion on the basis of id
     */
    @ApiOperation("get question of Questionnaire section by id ")
    @GetMapping("/question_section/question/{id}")
    public ResponseEntity<Object> getMasterQuestionById(@PathVariable Long countryId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.getMasterQuestion(countryId,  id));
    }

    /**
     * @param countryId
     * @return
     */
    @ApiOperation("get All question of Questionnaire section")
    @GetMapping("/question_section/question/all")
    public ResponseEntity<Object> getAllMasterQuestion(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.getAllMasterQuestion(countryId));
    }


    @DeleteMapping("/question_section/{sectionId}/question/delete/{id}")
    public ResponseEntity<Object> deleteMasterQuestion(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @PathVariable BigInteger sectionId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.deleteMasterQuestion(countryId, id,sectionId));
    }


}

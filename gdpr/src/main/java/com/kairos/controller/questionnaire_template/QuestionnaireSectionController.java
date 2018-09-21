package com.kairos.controller.questionnaire_template;

import com.kairos.dto.gdpr.QuestionnaireSectionDTO;
import com.kairos.service.questionnaire_template.QuestionnaireSectionService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class QuestionnaireSectionController {


    @Inject
    private QuestionnaireSectionService masterQuestionnaireSectionService;


    /**
     * @param countryId
     * @param templateId id of MAsterQuestionnaireTemplate
     * @param questionnaireSectionsDto
     * @return  master questionnaire template with questionnaire sections
     */
    @ApiOperation(value = "create and add questionnaire section to questionnaire template ")
    @PostMapping("/questionnaire_template/{templateId}/section")
    public ResponseEntity<Object> addMasterQuestionnaireSectionToQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable BigInteger templateId, @Validated @RequestBody ValidateRequestBodyList<QuestionnaireSectionDTO> questionnaireSectionsDto) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.addMasterQuestionnaireSectionToQuestionnaireTemplate(countryId, templateId, questionnaireSectionsDto.getRequestBody()));


    }

    /**
     *
     * @param countryId
     * @param id id of QuestionnaireSection
     * @return true with responseEntity on deletion
     */
    @ApiOperation("delete questionnaire section by id ")
    @DeleteMapping("/questionnaire_template/{templateId}/section/{id}")
    public ResponseEntity<Object> deleteMasterQuestionnaireSection(@PathVariable Long countryId,  @PathVariable BigInteger id,@PathVariable BigInteger templateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.deleteQuestionnaireSection(countryId, id,templateId));
    }


}

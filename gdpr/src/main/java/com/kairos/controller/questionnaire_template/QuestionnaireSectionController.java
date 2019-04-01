package com.kairos.controller.questionnaire_template;

import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireTemplateSectionDTO;
import com.kairos.service.questionnaire_template.QuestionnaireSectionService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
class QuestionnaireSectionController {


    @Inject
    private QuestionnaireSectionService questionnaireSectionService;


    @ApiOperation(value = "create and add questionnaire section to questionnaire template ")
    @PostMapping(COUNTRY_URL + "/questionnaire_template/{templateId}/section")
    public ResponseEntity<Object> addMasterQuestionnaireSectionToQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable Long templateId, @Validated @RequestBody QuestionnaireTemplateSectionDTO questionnaireSectionsDto) {

        if (CollectionUtils.isEmpty(questionnaireSectionsDto.getSections())) {
            return ResponseHandler.invalidResponse(HttpStatus.OK, true, "Create Section");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireSectionService.createOrUpdateQuestionnaireSectionAndAddToQuestionnaireTemplate(countryId, templateId, questionnaireSectionsDto, false));
    }


    @ApiOperation("delete questionnaire section by id ")
    @DeleteMapping(COUNTRY_URL + "/questionnaire_template/{templateId}/section/{sectionId}")
    public ResponseEntity<Object> deleteMasterQuestionnaireSection(@PathVariable Long countryId, @PathVariable Long templateId, @PathVariable Long sectionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireSectionService.deleteQuestionnaireSectionFromTemplate(false, countryId, templateId, sectionId));
    }


    @ApiOperation(value = "create and add questionnaire section to questionnaire template ")
    @PostMapping(UNIT_URL + "/questionnaire_template/{templateId}/section")
    public ResponseEntity<Object> saveQuestionnaireSectionToQuestionnaireTemplateOfUnit(@PathVariable Long unitId, @PathVariable Long templateId, @Validated @RequestBody QuestionnaireTemplateSectionDTO questionnaireSectionsDto) {
        if (CollectionUtils.isEmpty(questionnaireSectionsDto.getSections())) {
            return ResponseHandler.invalidResponse(HttpStatus.OK, true, "Create Section");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireSectionService.createOrUpdateQuestionnaireSectionAndAddToQuestionnaireTemplate(unitId, templateId, questionnaireSectionsDto, true));
    }

    @ApiOperation("delete questionnaire section by id ")
    @DeleteMapping(UNIT_URL + "/questionnaire_template/{templateId}/section/{sectionId}")
    public ResponseEntity<Object> deleteQuestionnaireSectionByUnitId(@PathVariable Long unitId, @PathVariable Long templateId, @PathVariable Long sectionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireSectionService.deleteQuestionnaireSectionFromTemplate(true, unitId, templateId, sectionId));
    }

}

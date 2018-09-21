package com.kairos.controller.questionnaire_template;

import com.kairos.dto.gdpr.QuestionnaireSectionDTO;
import com.kairos.service.questionnaire_template.QuestionnaireSectionService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class QuestionnaireSectionController {


    @Inject
    private QuestionnaireSectionService masterQuestionnaireSectionService;


    @ApiOperation(value = "create and add questionnaire section to questionnaire template ")
    @PostMapping(COUNTRY_URL + "/questionnaire_template/{templateId}/section")
    public ResponseEntity<Object> addMasterQuestionnaireSectionToQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable BigInteger templateId, @Validated @RequestBody ValidateRequestBodyList<QuestionnaireSectionDTO> questionnaireSectionsDto) {

        if (CollectionUtils.isEmpty(questionnaireSectionsDto.getRequestBody())) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,"Section List Must Not Be Empty\"" );
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.addMasterQuestionnaireSectionToQuestionnaireTemplate(countryId, templateId, questionnaireSectionsDto.getRequestBody()));
    }


    @ApiOperation("delete questionnaire section by id ")
    @DeleteMapping(COUNTRY_URL + "/questionnaire_template/{templateId}/section/{sectionId}")
    public ResponseEntity<Object> deleteMasterQuestionnaireSection(@PathVariable Long countryId, @PathVariable BigInteger templateId, @PathVariable BigInteger sectionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.deleteMasterQuestionnaireSection(countryId, templateId, sectionId));
    }


    @ApiOperation(value = "create and add questionnaire section to questionnaire template ")
    @PostMapping(UNIT_URL + "/questionnaire_template/{templateId}/section")
    public ResponseEntity<Object> saveQuestionnaireSectionToQuestionnaireTemplateOfUnit(@PathVariable Long unitId, @PathVariable BigInteger templateId, @Validated @RequestBody ValidateRequestBodyList<QuestionnaireSectionDTO> questionnaireSectionsDto) {
        if (CollectionUtils.isEmpty(questionnaireSectionsDto.getRequestBody())) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,"Section List Must Not Be Empty" );
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.createOrUpdateQuestionnaireSectionAndAddToQuestionnaireTemplateOfUnit(unitId, templateId, questionnaireSectionsDto.getRequestBody()));
    }

    @ApiOperation("delete questionnaire section by id ")
    @DeleteMapping(UNIT_URL + "/questionnaire_template/{templateId}/section/{sectionId}")
    public ResponseEntity<Object> deleteQuestionnaireSectionByUnitId(@PathVariable Long unitId, @PathVariable BigInteger templateId, @PathVariable BigInteger sectionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.deleteQuestionnaireSectionByUnitId(unitId, templateId, sectionId));
    }


}

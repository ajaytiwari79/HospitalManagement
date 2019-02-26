package com.kairos.controller.questionnaire_template;


import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireTemplateDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.service.questionnaire_template.QuestionnaireTemplateService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
class QuestionnaireTemplateController {


    @Inject
    private QuestionnaireTemplateService questionnaireTemplateService;


    @ApiOperation(value = "add questionnaire template basic data ")
    @PostMapping(COUNTRY_URL + "/questionnaire_template")
    public ResponseEntity<Object> addMasterQuestionnaireTemplate(@PathVariable Long countryId, @Valid @RequestBody QuestionnaireTemplateDTO templateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.saveMasterQuestionnaireTemplate(countryId, templateDto));

    }

    @ApiOperation(value = "get all questionnaire template basic response ")
    @GetMapping(COUNTRY_URL + "/questionnaire_template")
    public ResponseEntity<Object> getAllMasterQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.getAllQuestionnaireTemplateWithSectionOfCountryOrOrganization(countryId, false));
    }

    @ApiOperation(value = "get questionnaire template With Sections by Id ")
    @GetMapping(COUNTRY_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> getMasterQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long countryId, @PathVariable Long questionnaireTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.getQuestionnaireTemplateWithSectionsByTemplateIdAndCountryIdOrOrganisationId(countryId, questionnaireTemplateId, false));
    }

    @ApiOperation(value = "delete questionnaire template by id ")
    @DeleteMapping(COUNTRY_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> deleteMasterQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable Long questionnaireTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.deleteMasterQuestionnaireTemplate(countryId, questionnaireTemplateId));
    }

    @ApiOperation(value = "update basic detail of Questionnaire template ")
    @PutMapping(COUNTRY_URL + "/questionnaire_template/{id}")
    public ResponseEntity<Object> updateMasterQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable Long id, @Valid @RequestBody QuestionnaireTemplateDTO templateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.updateMasterQuestionnaireTemplate(countryId, id, templateDto));

    }

    @ApiOperation(value = "get Questionnaire template Attribute List Acc to Template type")
    @GetMapping("/questionnaire_template/attributes")
    public ResponseEntity<Object> getQuestionnaireTemplateAttributeNames(@RequestParam String templateType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.getQuestionnaireTemplateAttributeNames(templateType));
    }

    @ApiOperation(value = "get Questionnaire template Attribute List Acc to Template type")
    @GetMapping("/questionnaire_template/status")
    public ResponseEntity<Object> getQuestionnaireTemplateStatus() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, QuestionnaireTemplateStatus.values());
    }

    @ApiOperation(value = "save  questionnaire template basic data at organization level ")
    @PostMapping(UNIT_URL + "/questionnaire_template")
    public ResponseEntity<Object> saveQuestionnaireTemplate(@PathVariable Long unitId, @Valid @RequestBody QuestionnaireTemplateDTO templateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.saveQuestionnaireTemplate(unitId, templateDto));
    }

    @ApiOperation(value = "update basic detail of Questionnaire template at organization level ")
    @PutMapping(UNIT_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> updateQuestionnaireTemplate(@PathVariable Long unitId, @PathVariable Long questionnaireTemplateId, @Valid @RequestBody QuestionnaireTemplateDTO templateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.updateQuestionnaireTemplate(unitId, questionnaireTemplateId, templateDto));
    }

    @ApiOperation(value = "delete questionnaire template by id ")
    @DeleteMapping(UNIT_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> deleteQuestionnaireTemplate(@PathVariable Long unitId, @PathVariable Long questionnaireTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.deleteQuestionnaireTemplate(unitId, questionnaireTemplateId));
    }


    @ApiOperation(value = "get questionnaire template With Sections by Id of unit")
    @GetMapping(UNIT_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> getQuestionnaireTemplateWithSectionAndQuestionById(@PathVariable Long unitId, @PathVariable Long questionnaireTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.getQuestionnaireTemplateWithSectionsByTemplateIdAndCountryIdOrOrganisationId(unitId, questionnaireTemplateId, true));
    }


    @ApiOperation(value = "get all questionnaire template of unit ")
    @GetMapping(UNIT_URL + "/questionnaire_template")
    public ResponseEntity<Object> getAllQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, questionnaireTemplateService.getAllQuestionnaireTemplateWithSectionOfCountryOrOrganization(unitId, true));
    }

}

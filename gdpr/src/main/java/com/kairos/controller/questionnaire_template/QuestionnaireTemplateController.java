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
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class QuestionnaireTemplateController {


    @Inject
    private QuestionnaireTemplateService masterQuestionnaireTemplateService;


    @ApiOperation(value = "add questionnaire template basic data ")
    @PostMapping(COUNTRY_URL + "/questionnaire_template")
    public ResponseEntity<Object> addMasterQuestionnaireTemplate(@PathVariable Long countryId, @Valid @RequestBody QuestionnaireTemplateDTO templateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.saveMasterQuestionnaireTemplate(countryId, templateDto));

    }

    @ApiOperation(value = "get all questionnaire template basic response ")
    @GetMapping(COUNTRY_URL + "/questionnaire_template")
    public ResponseEntity<Object> getAllMasterQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getAllMasterQuestionnaireTemplateWithSection(countryId));
    }

    @ApiOperation(value = "get questionnaire template With Sections by Id ")
    @GetMapping(COUNTRY_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> getMasterQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long countryId, @PathVariable BigInteger questionnaireTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getMasterQuestionnaireTemplateWithSectionById(countryId, questionnaireTemplateId));
    }

    @ApiOperation(value = "delete questionnaire template by id ")
    @DeleteMapping(COUNTRY_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> deleteMasterQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable BigInteger questionnaireTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.deleteMasterQuestionnaireTemplate(countryId, questionnaireTemplateId));
    }

    @ApiOperation(value = "update basic detail of Questionnaire template ")
    @PutMapping(COUNTRY_URL + "/questionnaire_template/{id}")
    public ResponseEntity<Object> updateMasterQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody QuestionnaireTemplateDTO templateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.updateMasterQuestionnaireTemplate(countryId, id, templateDto));

    }

    @ApiOperation(value = "get Questionnaire template Attribute List Acc to Template type")
    @GetMapping("/questionnaire_template/attributes")
    public ResponseEntity<Object> getQuestionnaireTemplateAttributeNames(@RequestParam String templateType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getQuestionnaireTemplateAttributeNames(templateType));
    }

    @ApiOperation(value = "get Questionnaire template Attribute List Acc to Template type")
    @GetMapping("/questionnaire_template/status")
    public ResponseEntity<Object> getQuestionnaireTemplateStatus() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, QuestionnaireTemplateStatus.values());
    }

    @ApiOperation(value = "save  questionnaire template basic data at organization level ")
    @PostMapping(UNIT_URL + "/questionnaire_template")
    public ResponseEntity<Object> saveQuestionnaireTemplate(@PathVariable Long unitId, @Valid @RequestBody QuestionnaireTemplateDTO templateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.saveQuestionnaireTemplate(unitId, templateDto));
    }

    @ApiOperation(value = "update basic detail of Questionnaire template at organization level ")
    @PutMapping(UNIT_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> updateQuestionnaireTemplate(@PathVariable Long unitId, @PathVariable BigInteger questionnaireTemplateId, @Valid @RequestBody QuestionnaireTemplateDTO templateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.updateQuestionnaireTemplate(unitId, questionnaireTemplateId, templateDto));
    }

    @ApiOperation(value = "delete questionnaire template by id ")
    @DeleteMapping(UNIT_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> deleteQuestionnaireTemplate(@PathVariable Long unitId, @PathVariable BigInteger questionnaireTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.deleteQuestionnaireTemplate(unitId, questionnaireTemplateId));
    }


    @ApiOperation(value = "get questionnaire template With Sections by Id of unit")
    @GetMapping(UNIT_URL + "/questionnaire_template/{questionnaireTemplateId}")
    public ResponseEntity<Object> getQuestionnaireTemplateWithSectionAndQuestionById(@PathVariable Long unitId, @PathVariable BigInteger questionnaireTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getQuestionnaireTemplateWithSectionByUnitIdAndId(unitId, questionnaireTemplateId));
    }


    @ApiOperation(value = "get all questionnaire template of unit ")
    @GetMapping(UNIT_URL + "/questionnaire_template")
    public ResponseEntity<Object> getAllQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getAllQuestionnaireTemplateWithSectionByUnitId(unitId));
    }

}

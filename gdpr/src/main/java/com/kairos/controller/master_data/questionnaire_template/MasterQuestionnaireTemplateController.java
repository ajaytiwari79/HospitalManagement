package com.kairos.controller.master_data.questionnaire_template;


import com.kairos.dto.gdpr.master_data.MasterQuestionnaireTemplateDTO;
import com.kairos.service.master_data.questionnaire_template.MasterQuestionnaireTemplateService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class MasterQuestionnaireTemplateController {


    @Inject
    private MasterQuestionnaireTemplateService masterQuestionnaireTemplateService;


    /**
     * @param countryId
     * @param templateDto
     * @return masterQuestionnaireTemplate basic response
     */
    @ApiOperation(value = "add questionnaire template basic data ")
    @PostMapping("/questionnaire_template/add")
    public ResponseEntity<Object> addMasterQuestionnaireTemplate(@PathVariable Long countryId,@Valid @RequestBody MasterQuestionnaireTemplateDTO templateDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.addQuestionnaireTemplate(countryId,  templateDto));


    }

    /**
     * @param countryId
     * @return return List MasterQuestionnaireTemplate With MasterQuestionnaireSection list(which contain List of MasterQuestions)
     * @description method fetch all MasterQuestionnaireTemplate with MasterQuestionnaireSections list (which contain MasterQuestion list)
     */
    @ApiOperation(value = "get all questionnaire template basic response ")
    @GetMapping("/questionnaire_template/all")
    public ResponseEntity<Object> getAllMasterQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getAllMasterQuestionnaireTemplateWithSection(countryId));
    }


    /**
     * @param countryId
     * @param organizationId
     * @param id             id of MasterQuestionnaireTemplate
     * @return return MasterQuestionnaireTemplate With MasterQuestionnaireSection list(which contain List of MasterQuestions)
     */
    @ApiOperation(value = "get questionnaire template With Sections by Id ")
    @GetMapping("/questionnaire_template/{id}")
    public ResponseEntity<Object> getMasterQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getMasterQuestionnaireTemplateWithSectionById(countryId, id));
    }

    /**
     * @param countryId
     * @param id             id of MasterQuestionnaireTemplate
     * @return true on deletion of MasterQuestionnaire template
     */
    @ApiOperation(value = "delete questionnaire template by id ")
    @DeleteMapping("/questionnaire_template/delete/{id}")
    public ResponseEntity<Object> deleteMasterQuestionnaireTemplate(@PathVariable Long countryId,  @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.deleteMasterQuestionnaireTemplate(countryId,  id));
    }

    /**
     * @param countryId
     * @param id             id of MasterQuestionnaireTemplate
     * @param templateDto
     * @return return update masterQuestionnaireTemplate object
     */
    @ApiOperation(value = "update basic detail of Questionnaire template ")
    @PutMapping("/questionnaire_template/update/{id}")
    public ResponseEntity<Object> updateQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody MasterQuestionnaireTemplateDTO templateDto) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.updateQuestionnaireTemplate(countryId, id, templateDto));

    }


    @ApiOperation(value = "get Questionnaire template Attribute List Acc to Template type")
    @GetMapping("/questionnaire_template/attributes")
    public ResponseEntity<Object> getQuestionnaireTemplateAttributeNames(@PathVariable Long countryId, @RequestParam String templateType) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getQuestionnaireTemplateAttributeNames(templateType));
    }


}

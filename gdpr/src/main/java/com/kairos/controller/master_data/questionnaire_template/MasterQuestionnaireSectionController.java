package com.kairos.controller.master_data.questionnaire_template;

import com.kairos.dto.gdpr.master_data.MasterQuestionnaireSectionDTO;
import com.kairos.service.master_data.questionnaire_template.MasterQuestionnaireSectionService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class MasterQuestionnaireSectionController {


    @Inject
    private MasterQuestionnaireSectionService masterQuestionnaireSectionService;


    /**
     * @param countryId
     * @param templateId id of MAsterQuestionnaireTemplate
     * @param questionnaireSectionsDto
     * @return  master questionnaire template with questionnaire sections
     */
    @ApiOperation(value = "create and add questionnaire section to questionnaire template ")
    @PostMapping("/questionnaire_template/{templateId}/section")
    public ResponseEntity<Object> addMasterQuestionnaireSectionToQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable BigInteger templateId, @Validated @RequestBody ValidateRequestBodyList<MasterQuestionnaireSectionDTO> questionnaireSectionsDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.addMasterQuestionnaireSectionToQuestionnaireTemplate(countryId, templateId, questionnaireSectionsDto.getRequestBody()));


    }

    /**
     *
     * @param countryId
     * @param id id of MasterQuestionnaireSection
     * @return true with responseEntity on deletion
     */
    @ApiOperation("delete questionnaire section by id ")
    @DeleteMapping("/questionnaire_template/{templateId}/section/{id}")
    public ResponseEntity<Object> deleteMasterQuestionnaireSection(@PathVariable Long countryId,  @PathVariable BigInteger id,@PathVariable BigInteger templateId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.deleteQuestionnaireSection(countryId, id,templateId));
    }


}

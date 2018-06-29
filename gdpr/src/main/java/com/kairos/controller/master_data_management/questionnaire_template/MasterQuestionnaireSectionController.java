package com.kairos.controller.master_data_management.questionnaire_template;

import com.kairos.dto.master_data.MasterQuestionnaireSectionDTO;
import com.kairos.service.master_data_management.questionnaire_template.MasterQuestionnaireSectionService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_MASTER_QUESTIONNAIRE_TEMPLATE;

@RestController
@RequestMapping(API_MASTER_QUESTIONNAIRE_TEMPLATE)
@Api(API_MASTER_QUESTIONNAIRE_TEMPLATE)
public class MasterQuestionnaireSectionController {


    @Inject
    private MasterQuestionnaireSectionService masterQuestionnaireSectionService;


    /**
     * @param countryId
     * @param templateId
     * @param questionniareSectionsDto
     * @return
     */
    @PostMapping("/{templateId}/section")
    @ApiOperation(value = "create and add questionniare section to questionnaire template ")
    public ResponseEntity<Object> addMasterQuestionnaireSectionToQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger templateId, @Valid @RequestBody ValidateListOfRequestBody<MasterQuestionnaireSectionDTO> questionniareSectionsDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.addMasterQuestionnaireSectionToQuestionnaireTemplate(countryId, organizationId, templateId, questionniareSectionsDto.getRequestBody()));


    }


    @PutMapping("/{templateId}/section/update")
    @ApiOperation(value = "update list of Questionniare section and deleted section if deleted property is true")
    public ResponseEntity updateQuestionnaireSectionAndQuestions(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger templateId, @Valid @RequestBody ValidateListOfRequestBody<MasterQuestionnaireSectionDTO> questionniareSectionsDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.updateExistingQuestionniareSectionsAndCreateNewSectionsWithQuestions(countryId, organizationId, templateId, questionniareSectionsDto.getRequestBody()));


    }

    @ApiOperation("delete questionnaire section by id ")
    @DeleteMapping("/section/{id}")
    public ResponseEntity<Object> deleteMasterQuestionnaireSection(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireSectionService.deletedQuestionniareSection(countryId, organizationId, id));
    }


}

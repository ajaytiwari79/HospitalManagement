package com.kairos.controller.master_data_management.questionnaire_template;


import com.kairos.dto.master_data.MasterQuestionnaireTemplateDTO;
import com.kairos.service.master_data_management.questionnaire_template.MasterQuestionnaireTemplateService;
import com.kairos.utils.ResponseHandler;
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
public class MasterQuestionnaireTemplateController {


    @Inject
    private MasterQuestionnaireTemplateService masterQuestionnaireTemplateService;


    /**
     *
     * @param countryId
     * @param templateDto
     * @return masterQuestionnaireTemplate basic response
     */
    @PostMapping("/add")
    @ApiOperation(value = "add questionnaire template basic data ")
    public ResponseEntity<Object> addMasterQuestionnaireTemplate(@PathVariable Long countryId, @Valid @RequestBody MasterQuestionnaireTemplateDTO templateDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.addQuestionnaireTemplate(countryId,templateDto ));


    }


    @GetMapping("/all")
    @ApiOperation(value = "get all questionnaire template basic response ")
    public ResponseEntity<Object> getAllMasterQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getAllMasterQuestionniareTemplateWithSection(countryId));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "get all questionnaire template basic response ")
    public ResponseEntity<Object> getMasterQuestionnaireTemplateWithSectionAndQuestion(@PathVariable Long countryId,@PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        else if (id==null)
        {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getMasterQuestionniareTemplateWithSectionById(countryId,id));
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "delete questionnaire template by id ")
    public ResponseEntity<Object> deleteMasterQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, " id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.deleteMasterQuestionnaireTemplate(countryId, id));


    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "update basic detail of Questionniare template ")
    public ResponseEntity<Object> updateQuestionniareTemplate(@PathVariable  Long countryId,@PathVariable  BigInteger id,@Valid @RequestBody MasterQuestionnaireTemplateDTO templateDto)
    {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, " id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.updateQuestionniareTemplate(countryId, id,templateDto));

    }




    /**
     * @param countryId
     * @param id
     * @param masterQuestionnaireSectionDto
     * @return
     *//*
    @PostMapping("/{id}/add")
    @ApiOperation(value = "create and add questionniare section to  questionnaire template ")
    public ResponseEntity<Object> addMasterQuestionnaireSectionToQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody ValidateListOfRequestBody<MasterQuestionnaireSectionDTO> masterQuestionnaireSectionDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.addMasterQuestionnaireSectionToQuestionnaireTemplate(countryId, id, masterQuestionnaireSectionDto.getRequestBody()));


    }

*/
}

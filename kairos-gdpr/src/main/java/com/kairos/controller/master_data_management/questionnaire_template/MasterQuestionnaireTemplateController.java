package com.kairos.controller.master_data_management.questionnaire_template;


import com.kairos.dto.master_data.MasterQuestionnaireSectionDto;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.service.master_data_management.questionnaire_template.MasterQuestionnaireTemplateService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_MASTER_QUESTIONNAIRE_TEMPLATE;

@RestController
@RequestMapping(API_MASTER_QUESTIONNAIRE_TEMPLATE)
@Api(API_MASTER_QUESTIONNAIRE_TEMPLATE)
public class MasterQuestionnaireTemplateController {


    @Inject
    private MasterQuestionnaireTemplateService masterQuestionnaireTemplateService;


    @PostMapping("/add")
    public ResponseEntity<Object> addMasterQuestionnaireTemplate(@PathVariable Long countryId, @Valid @RequestBody MasterQuestionnaireTemplate masterQuestionnaireTemplate) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.addQuestionnaireTemplate(countryId, masterQuestionnaireTemplate));


    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllMasterQuestionnaireTemplate(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.getAllMasterQuestionniareTemplate(countryId));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteMasterQuestionnaireTemplate(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, " id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.deleteMasterQuestionnaireTemplate(countryId, id));


    }


    @PostMapping("/{id}/add")
    public ResponseEntity<Object> addMasterQuestionnaireSectionToQuestionnaireTemplate(@PathVariable Long countryId,@PathVariable  BigInteger id,@Valid @RequestBody ValidateListOfRequestBody<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDto)
    {
          if(countryId==null)
          {return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionnaireTemplateService.addMasterQuestionnaireSectionToQuestionnaireTemplate(countryId,id, masterQuestionnaireSectionDto.getRequestBody()));


    }


}

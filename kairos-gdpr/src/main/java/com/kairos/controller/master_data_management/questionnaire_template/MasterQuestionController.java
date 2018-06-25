package com.kairos.controller.master_data_management.questionnaire_template;


import com.kairos.dto.master_data.MasterQuestionDto;
import com.kairos.service.master_data_management.questionnaire_template.MasterQuestionService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.POST;

import java.math.BigInteger;
import java.util.Set;

import static com.kairos.constant.ApiConstant.API_MASTER_QUESTION;

@RestController
@RequestMapping(API_MASTER_QUESTION)
@Api(API_MASTER_QUESTION)
public class MasterQuestionController {


    @Inject
    private MasterQuestionService masterQuestionService;


   /* @PostMapping("/add")
    public ResponseEntity<Object> addMasterQuestion(@PathVariable Long countryId, @Valid @RequestBody ValidateListOfRequestBody<MasterQuestionDto> masterQuestionDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.addMasterQuestion(countryId, masterQuestionDto));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getMasterQuestion(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.getMasterQuestion(countryId, id));
    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllMasterQuestion(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.getAllMasterQuestion(countryId));
    }

    @GetMapping("/ids")
    public ResponseEntity<Object> getMasterQuestionListByIds(@PathVariable Long countryId, @RequestBody Set<BigInteger> ids) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.getMasterQuestionListByIds(countryId, ids));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteMasterQuestion(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.deleteMasterQuestion(id));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateMasterQuestion(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody MasterQuestionDto masterQuestionDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id cannot be null");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterQuestionService.updateMasterQuestion(countryId, id, masterQuestionDto));
    }

*/
}

package com.kairos.controller.agreement_template;


import com.kairos.persistance.model.agreement_template.AgreementTemplate;
import com.kairos.persistance.model.agreement_template.dto.AgreementTemplateDto;
import com.kairos.service.agreement_template.AgreementTemplateService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.kairos.constant.ApiConstant.API_AGREEMENT_TEMPLATE_URl;


@RestController
@RequestMapping(API_AGREEMENT_TEMPLATE_URl)
@Api(API_AGREEMENT_TEMPLATE_URl)
@CrossOrigin
public class AgreementTemplateController {


    @Inject
    private AgreementTemplateService agreementTemplateService;

    @ApiOperation(value = "create Agreement Template")
    @RequestMapping(value = "/default/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createDefaultAgreementTemplate(@Validated @RequestBody AgreementTemplateDto agreementTemplateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementTemplateService.createDefaultAgrementTemplate(agreementTemplateDto));

    }


    @ApiOperation("create Agreement Template")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createAgreementTemplate(@Validated @RequestBody AgreementTemplateDto agreementTemplateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementTemplateService.createAgrementTemplate(agreementTemplateDto));

    }


    @GetMapping("/agreement/id/{id}")
    public ResponseEntity<Object> getAgreementTemplateById(@PathVariable BigInteger id) {
        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementTemplateService.getAgreementTemplateById(id));

    }


    @DeleteMapping("delete/id/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable BigInteger id) {

        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementTemplateService.deleteById(id));

    }

    @PutMapping("/update/{id}")
    public   ResponseEntity<Object> updateAgreementtemplate(@PathVariable BigInteger id, @RequestBody List<BigInteger> clausesIds ) {
        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementTemplateService.updateAgreementTemplateclauses(id,clausesIds));

    }




}

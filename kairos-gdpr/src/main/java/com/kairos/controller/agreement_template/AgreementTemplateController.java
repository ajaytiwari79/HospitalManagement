package com.kairos.controller.agreement_template;

import com.kairos.dto.PolicyAgreementTemplateDto;
import com.kairos.persistance.model.enums.VersionNode;
import com.kairos.service.agreement_template.PolicyAgreementTemplateService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.math.BigInteger;
import static com.kairos.constant.ApiConstant.API_AGREEMENT_TEMPLATE_URl;


@RestController
@RequestMapping(API_AGREEMENT_TEMPLATE_URl)
@Api(API_AGREEMENT_TEMPLATE_URl)
@CrossOrigin
public class AgreementTemplateController {


    @Inject
    private PolicyAgreementTemplateService policyAgreementTemplateService;


    @ApiOperation("create Agreement Template")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createPolicyAgreementTemplate(@Validated @RequestBody PolicyAgreementTemplateDto agreementTemplateDto) throws RepositoryException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.createPolicyAgreementTemplate(agreementTemplateDto));

    }


    @GetMapping("/agreement/id/{id}")
    public ResponseEntity<Object> getPolicyAgreementTemplateById(@PathVariable BigInteger id) {
        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getPolicyAgreementTemplateById(id));

    }


    @DeleteMapping("delete/id/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable BigInteger id) {

        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.deletePolicyAgreementTemplate(id));

    }

    @PutMapping("/update/{id}")
    public   ResponseEntity<Object> updateAgreementtemplate(@PathVariable BigInteger id, @RequestBody PolicyAgreementTemplateDto policyAgreementTemplateDto ) throws RepositoryException {
        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.updatePolicyAgreementTemplate(id,policyAgreementTemplateDto));

    }



    @GetMapping("template/{id}")
    public   ResponseEntity<Object> policyDocumentVersion(@PathVariable BigInteger id, @RequestParam VersionNode version) throws RepositoryException {
        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true,policyAgreementTemplateService.getPolicyTemplateVersion(id,version));

    }



}

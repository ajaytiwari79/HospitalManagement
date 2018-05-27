package com.kairos.controller.agreement_template;

import com.kairos.dto.PolicyAgreementTemplateDto;
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
/*
 *
 *  created by bobby 10/5/2018
 * */


@RestController
@RequestMapping(API_AGREEMENT_TEMPLATE_URl)
@Api(API_AGREEMENT_TEMPLATE_URl)
public class PolicyAgreementTemplateController {


    @Inject
    private PolicyAgreementTemplateService policyAgreementTemplateService;


    @ApiOperation("create Agreement Template")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createPolicyAgreementTemplate(@PathVariable Long countryId,@Validated @RequestBody PolicyAgreementTemplateDto agreementTemplateDto) throws RepositoryException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.createPolicyAgreementTemplate(countryId,agreementTemplateDto));

    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getPolicyAgreementTemplateById(@PathVariable Long countryId,@PathVariable BigInteger id) {
        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getPolicyAgreementTemplateById(countryId,id));

    }


    @DeleteMapping("delete/{id}")
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



    @GetMapping("/all")
    public   ResponseEntity<Object> getPolicyAgreementTemplateWithData()
    {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,policyAgreementTemplateService.getPolicyAgreementTemplateWithData());

    }





    @GetMapping("/{id}/version")
    public   ResponseEntity<Object> getPolicyTemplateVersion(@PathVariable BigInteger id, @RequestParam String version) throws RepositoryException {
        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true,policyAgreementTemplateService.getPolicyTemplateVersion(id,version));

    }

    @GetMapping("/{id}/versions")
    public   ResponseEntity<Object> getPolicyTemplateAllVersionList(@PathVariable Long countryId,@PathVariable BigInteger id) throws RepositoryException {
        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true,policyAgreementTemplateService.getPolicyTemplateAllVersionList(countryId,id));

    }


}

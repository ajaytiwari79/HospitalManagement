package com.kairos.controller.agreement_template;

import com.kairos.dto.PolicyAgreementTemplateDTO;
import com.kairos.service.agreement_template.PolicyAgreementTemplateService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.math.BigInteger;
import static com.kairos.constants.ApiConstant.API_AGREEMENT_TEMPLATE_URl;
/*
 *
 *  created by bobby 10/5/2018
 * */


@RestController
@RequestMapping(API_AGREEMENT_TEMPLATE_URl)
@Api(API_AGREEMENT_TEMPLATE_URl)
public class PolicyAgreementTemplateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyAgreementTemplateController.class);

    @Inject
    private PolicyAgreementTemplateService policyAgreementTemplateService;


    @ApiOperation("create Agreement Template")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createPolicyAgreementTemplate(@PathVariable Long countryId,@PathVariable Long organizationId,@Validated @RequestBody PolicyAgreementTemplateDTO agreementTemplateDto) throws RepositoryException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.createPolicyAgreementTemplate(countryId,organizationId,agreementTemplateDto));

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
    public   ResponseEntity<Object> updateAgreementtemplate(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id, @RequestBody PolicyAgreementTemplateDTO policyAgreementTemplateDto ) throws RepositoryException {
        if (id==null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "agreement template id cannot be null or empty");
        }
        else if (countryId==null)
        {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id cannot be null or empty");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.updatePolicyAgreementTemplate(countryId,organizationId,id,policyAgreementTemplateDto));

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

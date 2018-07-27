package com.kairos.controller.agreement.cta;

import com.kairos.persistence.model.agreement.cta.CTARuleTemplateDTO;
import com.kairos.persistence.model.agreement.cta.cta_response.CollectiveTimeAgreementDTO;
import com.kairos.service.agreement.cta.CostTimeAgreementService;
import com.kairos.service.agreement.cta.CountryCTAService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.kairos.constants.ApiConstants.*;

@RequestMapping(API_ORGANIZATION_URL)
@RestController
public class CostTimeAgreementController {
    @Autowired
  private  CostTimeAgreementService costTimeAgreementService;
    @Inject private CountryCTAService countryCTAService;

    /**
     * @auther anil maurya
     * @param countryId
     * @return
     */
    @RequestMapping(value = "/country/{countryId}/cta", method = RequestMethod.POST)
    @ApiOperation("Create CTA")
    public ResponseEntity<Map<String, Object>> createCTA(@PathVariable Long countryId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO ) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                countryCTAService.createCostTimeAgreementInCountry(countryId,collectiveTimeAgreementDTO));
    }

    @RequestMapping(value = "/country/{countryId}/cta/{ctaId}", method = RequestMethod.PUT)
    @ApiOperation("Update CTA")
    public ResponseEntity<Map<String, Object>> updateCTA(@PathVariable Long countryId, @PathVariable Long ctaId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO ) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                countryCTAService.updateCostTimeAgreement(countryId, null, ctaId, collectiveTimeAgreementDTO));
    }

    @RequestMapping(value = "/unit/{unitId}/cta/{ctaId}", method = RequestMethod.PUT)
    @ApiOperation("Update CTA Of Unit")
    public ResponseEntity<Map<String, Object>> updateUnitCTA(@PathVariable Long unitId, @PathVariable Long ctaId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO ) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                countryCTAService.updateCostTimeAgreement(null, unitId, ctaId, collectiveTimeAgreementDTO));
    }

    @RequestMapping(value = "/country/{countryId}/cta/{ctaId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete CTA")
    public ResponseEntity<Map<String, Object>> deleteCTA(@PathVariable Long countryId, @PathVariable Long ctaId) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.deleteCostTimeAgreement(countryId, ctaId));
    }

    @RequestMapping(value = "/country/{countryId}/cta", method = RequestMethod.GET)
    @ApiOperation("GET CTA")
    public ResponseEntity<Map<String, Object>> getCTA(@PathVariable Long countryId) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.loadAllCTAByCountry(countryId));
    }

    @RequestMapping(value = "/unit/{unitId}/cta", method = RequestMethod.GET)
    @ApiOperation("GET CTA Of Unit")
    public ResponseEntity<Map<String, Object>> getCTAOfUnit(@PathVariable Long unitId) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.loadAllCTAByUnit(unitId));
    }

    @RequestMapping(value = "/country/{countryId}/cta_rule_template", method = RequestMethod.POST)
    @ApiOperation("Create CTA Rule Template")
    public ResponseEntity<Map<String, Object>> createCTARuleTemplate(@PathVariable Long countryId
            , @RequestBody @Valid CTARuleTemplateDTO ctaRuleTemplateDTO ) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.createCTARuleTemplate(countryId,ctaRuleTemplateDTO));
    }

    @RequestMapping(value = "/country/{countryId}/cta_rule_template/{templateId}", method = RequestMethod.PUT)
    @ApiOperation("Update CTA Rule Template")
    public ResponseEntity<Map<String, Object>> updateCTARuleTemplate(@PathVariable Long countryId,@PathVariable Long templateId
            , @RequestBody @Valid CTARuleTemplateDTO ctaRuleTemplateDTO ) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.updateCTARuleTemplate(countryId,templateId, ctaRuleTemplateDTO));
    }

    @RequestMapping(value = "/unit/{unitId}/cta/expertise", method = RequestMethod.GET)
    @ApiOperation("get expertise for cta_response rule template")
    public ResponseEntity<Map<String, Object>> getExpertiseForCTA(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.getExpertiseForOrgCTA(unitId));
    }

    @ApiOperation(value = "Update Unit Position's CTA")
    @PutMapping(value = UNIT_URL+"/unit_position/{unitPositionId}/cta/{ctaId}")
    public ResponseEntity<Map<String, Object>> updateCostTimeAgreementForUnitPosition(@PathVariable Long unitPositionId, @PathVariable Long unitId, @PathVariable Long ctaId, @RequestBody @Valid CollectiveTimeAgreementDTO ctaDTO) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.updateCostTimeAgreementForUnitPosition(unitId, unitPositionId, ctaId, ctaDTO));
    }

    @ApiOperation(value = "get unit_position's CTA")
    @GetMapping(value = UNIT_URL+"/unit_position/{unitPositionId}/cta")
    public ResponseEntity<Map<String, Object>> getUnitEmploymentPositionCTA(@PathVariable Long unitPositionId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.getUnitPositionCTA(unitId, unitPositionId));
    }

    @RequestMapping(value = "unit/{unitId}/copy_unit_cta", method = RequestMethod.POST)
    @ApiOperation("Create copy of CTA at unit")
    public ResponseEntity<Map<String, Object>> createCopyOfUnitCTA(@PathVariable Long unitId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO ) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                costTimeAgreementService.createCopyOfUnitCTA(unitId,collectiveTimeAgreementDTO));
    }

    @ApiOperation(value = "Get CTA by Organization sub type  by using sub type Id")
    @RequestMapping(value = COUNTRY_URL + "/cta/organization_sub_type/{organizationSubTypeId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllCTAByOrganizationSubType(@PathVariable long organizationSubTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.getAllCTAByOrganizationSubType(organizationSubTypeId));
    }

    @ApiOperation(value = "link and unlink cta with org sub-type")
    @PutMapping(value = COUNTRY_URL + "/organization_sub_type/{organizationSubTypeId}/cta/{ctaId}")
    public ResponseEntity<Map<String, Object>> setCTAWithOrganizationType(@PathVariable long countryId, @PathVariable long ctaId, @RequestBody CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, @PathVariable long organizationSubTypeId, @RequestParam(value = "checked") boolean checked) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.setCTAWithOrganizationType(countryId, ctaId,collectiveTimeAgreementDTO, organizationSubTypeId, checked));
    }

}

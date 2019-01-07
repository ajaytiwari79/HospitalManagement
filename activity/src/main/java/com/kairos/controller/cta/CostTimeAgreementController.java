package com.kairos.controller.cta;

import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.cta.CollectiveTimeAgreementDTO;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.cta.CountryCTAService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@RequestMapping(API_ORGANIZATION_URL)
@RestController
public class CostTimeAgreementController {
    @Inject
    private CostTimeAgreementService costTimeAgreementService;
    @Inject
    private CountryCTAService countryCTAService;

    /**
     *
     * @param countryId
     * @param collectiveTimeAgreementDTO
     * @return
     */
    @PostMapping(value = "/country/{countryId}/cta")
    @ApiOperation("Create CTA")
    public ResponseEntity<Map<String, Object>> createCTA(@PathVariable Long countryId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO )  {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                countryCTAService.createCostTimeAgreementInCountry(countryId,collectiveTimeAgreementDTO,false));
    }

    /**
     *
     * @param countryId
     * @param ctaId
     * @param collectiveTimeAgreementDTO
     * @return
     */
    @PutMapping(value = "/country/{countryId}/cta/{ctaId}")
    @ApiOperation("Update CTA")
    public ResponseEntity<Map<String, Object>> updateCTA(@PathVariable Long countryId, @PathVariable BigInteger ctaId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO )  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                countryCTAService.updateCostTimeAgreementInCountry(countryId,  ctaId, collectiveTimeAgreementDTO));
    }

    /**
     *
     * @param unitId
     * @param ctaId
     * @param collectiveTimeAgreementDTO
     * @return
     */
    @PutMapping(value = "/unit/{unitId}/cta/{ctaId}")
    @ApiOperation("Update CTA Of Unit")
    public ResponseEntity<Map<String, Object>> updateUnitCTA(@PathVariable Long unitId, @PathVariable BigInteger ctaId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO )  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                countryCTAService.updateCostTimeAgreementInUnit( unitId, ctaId, collectiveTimeAgreementDTO));
    }

    /**
     *
     * @param countryId
     * @param ctaId
     * @return
     */
    @DeleteMapping(value = "/country/{countryId}/cta/{ctaId}")
    @ApiOperation("Delete CTA")
    public ResponseEntity<Map<String, Object>> deleteCTA(@PathVariable Long countryId, @PathVariable BigInteger ctaId)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.deleteCostTimeAgreement(countryId, ctaId));
    }

    /**
     *
     * @param countryId
     * @return
     */
    @GetMapping(value = "/country/{countryId}/cta")
    @ApiOperation("GET CTA")
    public ResponseEntity<Map<String, Object>> getCTA(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.loadAllCTAByCountry(countryId));
    }

    /**
     *
     * @param unitId
     * @return
     */
    @GetMapping(value = "/unit/{unitId}/cta")
    @ApiOperation("GET CTA Of Unit")
    public ResponseEntity<Map<String, Object>> getCTAOfUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.loadAllCTAByUnit(unitId));
    }

    /**
     *
     * @param unitId
     * @param ctaId
     * @return
     */
    @GetMapping(value = "/unit/{unitId}/cta/{ctaId}/rule-templates")
    @ApiOperation("GET CTA RuleTemplate By ctaId")
    public ResponseEntity<Map<String, Object>> getCTARuleTemplateOfUnit(@PathVariable Long unitId,@PathVariable BigInteger ctaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.getCTARuleTemplateOfUnit(unitId,ctaId));
    }


    /**
     *
     * @param unitPositionId
     * @param unitId
     * @param ctaId
     * @param ctaDTO
     * @return
     */
    @ApiOperation(value = "Update Unit Position's CTA")
    @PutMapping(value = UNIT_URL+"/unit_position/{unitPositionId}/cta/{ctaId}")
    public ResponseEntity<Map<String, Object>> updateCostTimeAgreementForUnitPosition(@PathVariable Long unitPositionId, @PathVariable Long unitId, @PathVariable BigInteger ctaId, @RequestBody @Valid CollectiveTimeAgreementDTO ctaDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.updateCostTimeAgreementForUnitPosition(unitId, unitPositionId, ctaId, ctaDTO));
    }


    /**
     *
     * @param unitPositionId
     * @param unitId
     * @return
     */
    @ApiOperation(value = "get unit_position's CTA")
    @GetMapping(value = UNIT_URL+"/unit_position/{unitPositionId}/cta")
    public ResponseEntity<Map<String, Object>> getUnitEmploymentPositionCTA(@PathVariable Long unitPositionId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.getUnitPositionCTA(unitId, unitPositionId));
    }


    /**
     *
     * @param countryId
     * @param ctaId
     * @return
     */
    @GetMapping(value = "/country/{countryId}/cta/{ctaId}/rule-templates")
    @ApiOperation("GET CTA Ruletemplate By ctaId in Country")
    public ResponseEntity<Map<String, Object>> getCTARuleTemplateOfCountry(@PathVariable Long countryId,@PathVariable BigInteger ctaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.getCTARuleTemplateOfCountry(countryId,ctaId));
    }

    /**
     *
     * @param countryId
     * @param ctaRuleTemplateDTO
     * @return
     */
    @PostMapping(value = "/country/{countryId}/cta_rule_template")
    @ApiOperation("Create CTA Rule Template")
    public ResponseEntity<Map<String, Object>> createCTARuleTemplate(@PathVariable Long countryId
            , @RequestBody @Valid CTARuleTemplateDTO ctaRuleTemplateDTO )  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.createCTARuleTemplate(countryId,ctaRuleTemplateDTO));
    }

    /**
     *
     * @param countryId
     * @param templateId
     * @param ctaRuleTemplateDTO
     * @return
     */
    @PutMapping(value = "/country/{countryId}/cta_rule_template/{templateId}")
    @ApiOperation("Update CTA Rule Template")
    public ResponseEntity<Map<String, Object>> updateCTARuleTemplate(@PathVariable Long countryId,@PathVariable BigInteger templateId
            , @RequestBody @Valid CTARuleTemplateDTO ctaRuleTemplateDTO )  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                costTimeAgreementService.updateCTARuleTemplate(countryId,templateId, ctaRuleTemplateDTO));
    }

    /**
     *
     * @param unitId
     * @param collectiveTimeAgreementDTO
     * @return
     */
    @PostMapping(value = "unit/{unitId}/copy_unit_cta")
    @ApiOperation("Create copy of CTA at unit")
    public ResponseEntity<Map<String, Object>> createCopyOfUnitCTA(@PathVariable Long unitId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO )  {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                costTimeAgreementService.createCopyOfUnitCTA(unitId,collectiveTimeAgreementDTO));
    }

    /**
     *
     * @param organizationSubTypeId
     * @return
     */
    @ApiOperation(value = "Get CTA by Organization sub type  by using sub type Id")
    @GetMapping(value = COUNTRY_URL + "/cta/organization_sub_type/{organizationSubTypeId}")
    public ResponseEntity<Map<String, Object>> getAllCTAByOrganizationSubType(@PathVariable Long countryId,@PathVariable long organizationSubTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.getAllCTAByOrganizationSubType(countryId,organizationSubTypeId));
    }

    /**
     *
     * @param countryId
     * @param ctaId
     * @param collectiveTimeAgreementDTO
     * @param organizationSubTypeId
     * @param checked
     * @return
     */
    @ApiOperation(value = "link and unlink cta with org sub-type")
    @PutMapping(value = COUNTRY_URL + "/organization_sub_type/{organizationSubTypeId}/cta/{ctaId}")
    public ResponseEntity<Map<String, Object>> setCTAWithOrganizationType(@PathVariable long countryId, @PathVariable BigInteger ctaId, @RequestBody CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, @PathVariable long organizationSubTypeId, @RequestParam(value = "checked") boolean checked)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.setCTAWithOrganizationType(countryId, ctaId,collectiveTimeAgreementDTO, organizationSubTypeId, checked));
    }

    /**
     *
     * @param countryId
     * @return
     */
    @ApiOperation(value = "create default cta ruletemplate ")
    @PostMapping(value = COUNTRY_URL + "/default_cta")
    public ResponseEntity<Map<String, Object>> createDefaultCtaRuleTemplate(@PathVariable long countryId)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.createDefaultCtaRuleTemplate(countryId));
    }


    @ApiOperation(value = "get versions cta")
    @GetMapping(value = UNIT_URL + "/get_versions_cta")
    public ResponseEntity<Map<String, Object>> getVersionsCTA(@PathVariable Long unitId,@RequestParam List<Long> upIds)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.getVersionsCTA(unitId,upIds));
    }

    @ApiOperation(value = "get default cta")
    @GetMapping(value = UNIT_URL + "/get_default_cta/expertise/{expertiseId}")
    public ResponseEntity<Map<String, Object>> getDefaultCTA(@PathVariable Long unitId,@PathVariable Long expertiseId)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, costTimeAgreementService.getDefaultCTA(unitId,expertiseId));
    }

    /**
     *
     * @param unitId
     * @param collectiveTimeAgreementDTO
     * @return
     */
    @PostMapping(value =UNIT_URL + "/cta")
    @ApiOperation("Create CTA in Organization")
    public ResponseEntity<Map<String, Object>> createCtaInOrganization(@PathVariable Long unitId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO )  {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                countryCTAService.createCostTimeAgreementInOrganization(unitId,collectiveTimeAgreementDTO));
    }

    @ApiOperation(value = "update phase in cta")
    @PostMapping(value = "/update_phase_in_cta")
    public ResponseEntity<Map<String, Object>> updatePhaseInCTA()  {
        costTimeAgreementService.updateExistingPhaseIdOfCTA();
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }


}


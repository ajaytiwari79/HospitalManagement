package com.kairos.service.cta;

import com.kairos.activity.cta.*;
import com.kairos.activity.wta.rule_template_category.RuleTemplateCategoryDTO;
import com.kairos.enums.FixedValueType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.RuleTemplateCategoryType;
import com.kairos.persistence.model.cta.CTARuleTemplate;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.wta.Expertise;
import com.kairos.persistence.model.wta.Organization;
import com.kairos.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.repository.cta.CTARuleTemplateRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.wta.rule_template.RuleTemplateCategoryRepository;
import com.kairos.rest_client.CountryRestClient;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.country.basic_details.CountryDTO;
import com.kairos.user.country.experties.ExpertiseResponseDTO;
import com.kairos.user.organization.OrganizationDTO;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.util.userContext.UserContext;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static javax.management.timer.Timer.ONE_DAY;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@Transactional
@Service
public class CostTimeAgreementService extends MongoBaseService {
    private final Logger logger = LoggerFactory.getLogger(CostTimeAgreementService.class);


    @Inject private RuleTemplateCategoryRepository ruleTemplateCategoryRepository;
    @Inject private CountryRestClient countryRestClient;
    @Inject private CTARuleTemplateRepository ctaRuleTemplateRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private CountryCTAService countryCTAService;
    @Inject private OrganizationRestClient organizationRestClient;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject private GenericRestClient genericRestClient;

    

    public void createDefaultCtaRuleTemplate(Long countryId) {
        RuleTemplateCategory category = ruleTemplateCategoryRepository
                .findByName(countryId, "NONE", RuleTemplateCategoryType.CTA);
        if (category != null) {
            CountryDTO country = countryRestClient.getCountryById(countryId);
            if(country!=null) {
                List<CTARuleTemplate> ctaRuleTemplates = createDefaultRuleTemplate(countryId, country.getCurrencyId(), category.getId());
                save(ctaRuleTemplates);
            }
        } else {
            logger.info("default CTARuleTemplateCategory is not exist");
        }

    }

    public CTARuleTemplateDTO createCTARuleTemplate(Long countryId, CTARuleTemplateDTO ctaRuleTemplateDTO) throws ExecutionException, InterruptedException {
        if (ctaRuleTemplateRepository.isCTARuleTemplateExistWithSameName(countryId, ctaRuleTemplateDTO.getName())) {
            exceptionService.dataNotFoundByIdException("message.cta.ruleTemplate.alreadyExist", ctaRuleTemplateDTO.getName());

        }
        CountryDTO countryDTO = countryRestClient.getCountryById(countryId);
        ctaRuleTemplateDTO.setId(null);
        CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate();
        Long userId = UserContext.getUserDetails().getId();
        ctaRuleTemplateDTO.setRuleTemplateType(ctaRuleTemplateDTO.getName());
        this.buildCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO, false,countryDTO);
        ctaRuleTemplate.setLastModifiedBy(userId);
        ctaRuleTemplate.setCountryId(countryId);
        this.save(ctaRuleTemplate);
        ctaRuleTemplateDTO.setId(ctaRuleTemplate.getId());
        return ctaRuleTemplateDTO;
    }

    private List<CTARuleTemplate> createDefaultRuleTemplate(Long countryId, Long currencyId, BigInteger ruleTemplateCategoryId) {
        List<CTARuleTemplate> ctaRuleTemplates = new ArrayList<>(10);
        CompensationTable compensationTable = new CompensationTable(10);
        FixedValue fixedValue = new FixedValue(10, currencyId, FixedValueType.PER_ACTIVITY);
        CalculateValueAgainst calculateValueAgainst = new CalculateValueAgainst(CalculateValueAgainst.CalculateValueType.FIXED_VALUE, 10.5f, fixedValue);
        PlannedTimeWithFactor plannedTimeWithFactor = new PlannedTimeWithFactor(10, true, AccountType.DUTYTIME_ACCOUNT);
        CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate("Working Evening Shifts",
                "CTA rule for evening shift, from 17-23 o'clock.  For this organization/unit this is payroll type '210:  Evening compensation'",
                "210:  Evening compensation", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);

        ctaRuleTemplates.add(ctaRuleTemplate);

        ctaRuleTemplate = new CTARuleTemplate("Working Night Shifts",
                "CTA rule for night shift, from 23-07 o. clock.  For this organization/unit this is payroll type “212:  Night compensation”",
                "212:  Night compensation", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);

        ctaRuleTemplates.add(ctaRuleTemplate);

        ctaRuleTemplate = new CTARuleTemplate("Working On a Saturday",
                "CTA rule for Saturdays shift, from 08-24 o. clock. For this organization/unit this is payroll type " +
                        "“214:  Saturday compensation”. If you are working from 00-07 on Saturday, you only gets evening " +
                        "compensation",
                "214:  Saturday compensation", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);

        ctaRuleTemplates.add(ctaRuleTemplate);

        ctaRuleTemplate = new CTARuleTemplate("Working On a Sunday",
                "CTA rule for Saturdays shift, from 00-24 o. clock. For this organization/unit this is " +
                        "payroll type “214:Saturday compensation”.All working time on Sundays gives compensation"
                ,
                "214:Saturday compensation", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);

        ctaRuleTemplates.add(ctaRuleTemplate);

        ctaRuleTemplate = new CTARuleTemplate("Working On a Full Public Holiday",
                "CTA rule for full public holiday shift, from 00-24 o. clock.  For this organization/unit this is " +
                        "payroll type “216:  public holiday compensation”. All working time on full PH gives " +
                        "compensation",
                "216:public holiday compensation", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);

        ctaRuleTemplates.add(ctaRuleTemplate);

        ctaRuleTemplate = new CTARuleTemplate("Working On a Half Public Holiday",
                "CTA rule for full public holiday shift, from 12-24 o. clock. For this organization/unit" +
                        " this is payroll type “218:  half public holiday compensation”.All working time on " +
                        "half PH gives compensation",
                "218: half public holiday compensation", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);

        ctaRuleTemplates.add(ctaRuleTemplate);

        ctaRuleTemplate = new CTARuleTemplate("Working Overtime",
                "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                        " 50% overtime compensation”.",
                "230:50% overtime compensation", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);
        ctaRuleTemplate = new CTARuleTemplate("Working Extratime",
                "CTA rule for extra time shift, from 00-24 o. clock.  For this organization/unit this is payroll type" +
                        " “250:  extratime compensation”. ",
                "250:  extratime compensation", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);

        ctaRuleTemplates.add(ctaRuleTemplate);

        ctaRuleTemplate = new CTARuleTemplate("Late Notice Compensation",
                "CTA rule for late notification on changes to working times.  If notice of change is done within 72 hours" +
                        " before start of working day, then staff is entitled to at compensation of 105 kroner",
                "", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);

        ctaRuleTemplates.add(ctaRuleTemplate);

        ctaRuleTemplate = new CTARuleTemplate("Extra Dutyfree Day For Each Public Holiday",
                "CTA rule for each public holiday.  Whenever there is a public holiday staff are entitled to an" +
                        " extra day off, within 3 month or just compensated in the timebank.",
                "", "xyz", ruleTemplateCategoryId,CalculationUnit.HOURS,compensationTable,calculateValueAgainst,ApprovalWorkFlow.NO_APPROVAL_NEEDED,BudgetType.ACTIVITY_COST,ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE,PlanningCategory.DEVIATION_FROM_PLANNED,plannedTimeWithFactor,countryId);

        ctaRuleTemplates.add(ctaRuleTemplate);

        return ctaRuleTemplates;


    }


    public CTARuleTemplateCategoryWrapper loadAllCTARuleTemplateByCountry(Long countryId) {
        List<RuleTemplateCategory> ruleTemplateCategories = ruleTemplateCategoryRepository.getRuleTemplateCategoryByCountry(countryId,RuleTemplateCategoryType.CTA);
        List<RuleTemplateCategoryDTO> ctaRuleTemplateCategoryList = ObjectMapperUtils.copyPropertiesOfListByMapper(ruleTemplateCategories,RuleTemplateCategoryDTO.class);
        List<BigInteger> ruleTemplateCategoryIds = ctaRuleTemplateCategoryList.parallelStream().map(RuleTemplateCategoryDTO::getId).collect(Collectors.toList());

        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = ctaRuleTemplateRepository.findByRuleTemplateCategoryIdInAndCountryAndDeletedFalse(ruleTemplateCategoryIds, countryId);
        CTARuleTemplateCategoryWrapper ctaRuleTemplateCategoryWrapper = new CTARuleTemplateCategoryWrapper();
        ctaRuleTemplateCategoryWrapper.getRuleTemplateCategories().addAll(ctaRuleTemplateCategoryList);
        ctaRuleTemplateCategoryWrapper.setRuleTemplates(ctaRuleTemplateDTOS);
        return ctaRuleTemplateCategoryWrapper;
    }

    public CTARuleTemplateCategoryWrapper loadAllCTARuleTemplateByUnit(Long unitId) {
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        return loadAllCTARuleTemplateByCountry(countryId);
    }

    private CTARuleTemplate buildCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO, Boolean doUpdate,CountryDTO countryDTO){
        ObjectMapperUtils.copyPropertiesUsingBeanUtils(ctaRuleTemplateDTO, ctaRuleTemplate, "calculateOnDayTypes");
        ctaRuleTemplate.setEmploymentTypes(ctaRuleTemplateDTO.getEmploymentTypes());
        RuleTemplateCategory ruleTemplateCategory = ruleTemplateCategoryRepository.findOne(ctaRuleTemplateDTO.getRuleTemplateCategory());
        ctaRuleTemplate.setRuleTemplateCategoryId(ruleTemplateCategory.getId());
        setActivityBasesCostCalculationSettings(ctaRuleTemplate);
        if (ctaRuleTemplate.getCalculateValueAgainst() != null && ctaRuleTemplate.getCalculateValueAgainst().getCalculateValue() != null) {
            switch (ctaRuleTemplate.getCalculateValueAgainst().getCalculateValue().toString()) {
                case "FIXED_VALUE": {
                    if (doUpdate && ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().getCurrencyId() != null) {
                        ctaRuleTemplate.getCalculateValueAgainst().getFixedValue().setCurrency(countryDTO.getCurrencyId());
                    }
                    break;
                }

                case "WEEKLY_HOURS": {
                    ctaRuleTemplate.getCalculateValueAgainst().setScale(ctaRuleTemplate.getCalculateValueAgainst().getScale());
                    break;
                }

                case "WEEKLY_SALARY":
                    ctaRuleTemplate.getCalculateValueAgainst().setScale(ctaRuleTemplate.getCalculateValueAgainst().getScale());
                    break;
            }
        }
        ctaRuleTemplate.getCalculateValueAgainst().setCalculateValue(ctaRuleTemplateDTO.getCalculateValueAgainst().getCalculateValue());
        logger.info("ctaRuleTemplate.getCalculateValueAgainst().getScale : {}", ctaRuleTemplate.getCalculateValueAgainst().getScale());
        return ctaRuleTemplate;
    }




    public Boolean deleteCostTimeAgreement(Long countryId, Long ctaId) {
        CostTimeAgreement costTimeAgreement = costTimeAgreementRepository.findCTAByCountryAndIdAndDeleted(countryId, ctaId, false);
        if (costTimeAgreement == null) {
            exceptionService.dataNotFoundByIdException("message.cta.id.notFound", ctaId);
        }
        costTimeAgreement.setDeleted(true);
        this.save(costTimeAgreement);
        return true;
    }






    public List<CTAResponseDTO> loadAllCTAByCountry(Long countryId) {
        List<CTAResponseDTO> costTimeAgreements = costTimeAgreementRepository.findCTAByCountryId(countryId);
        return costTimeAgreements;
    }

    public List<CTAResponseDTO> loadAllCTAByUnit(Long unitId) {
//        Country country = countryRestClient.findOne(countryId);
        return costTimeAgreementRepository.findCTAByUnitId(unitId);
    }

    public CTARuleTemplate saveEmbeddedEntitiesOfCTARuleTemplate(CTARuleTemplate ctaRuleTemplate, CTARuleTemplateDTO ctaRuleTemplateDTO) {
        if (ctaRuleTemplate.getId() != null) {
            ctaRuleTemplate.setTimeTypeIds(null);
        }
        ctaRuleTemplate.setEmploymentTypes(ctaRuleTemplateDTO.getEmploymentTypes());
        BigInteger ruleTemplateId = ctaRuleTemplateDTO.getRuleTemplateCategory();
        if (ruleTemplateId != null) {
            ctaRuleTemplate.setRuleTemplateCategoryId(ruleTemplateId);
        }
        return ctaRuleTemplate;
    }


    private void buildCTA(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO){
        // Get Rule Templates
        List<CTARuleTemplate> ctaRuleTemplates = new ArrayList<>(collectiveTimeAgreementDTO.getRuleTemplates().size());
            for (CTARuleTemplateDTO ctaRuleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
                CTARuleTemplate ctaRuleTemplate = new CTARuleTemplate();
                BeanUtils.copyProperties(ctaRuleTemplateDTO, ctaRuleTemplate);
                setActivityBasesCostCalculationSettings(ctaRuleTemplate);
                ctaRuleTemplate = saveEmbeddedEntitiesOfCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO);
                ctaRuleTemplates.add(ctaRuleTemplate);
            }
        save(ctaRuleTemplates);
        List<BigInteger> ruleTemplateIds = ctaRuleTemplates.stream().map(c->c.getId()).collect(Collectors.toList());
        costTimeAgreement.setRuleTemplateIds(ruleTemplateIds);
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());
    }

    private void setActivityBasesCostCalculationSettings(CTARuleTemplate ctaRuleTemplate) {

        switch (ctaRuleTemplate.getActivityTypeForCostCalculation()) {
            case TIME_TYPE_ACTIVITY:
                ctaRuleTemplate.setActivityIds(new ArrayList<>());
                break;
            default:
                ctaRuleTemplate.setPlannedTimeIds(null);
                ctaRuleTemplate.setTimeTypeIds(null);
                break;
        }
    }



    public CTARuleTemplateDTO updateCTARuleTemplate(Long countryId, BigInteger id, CTARuleTemplateDTO ctaRuleTemplateDTO) throws ExecutionException, InterruptedException {
        CountryDTO countryDTO = countryRestClient.getCountryById(countryId);
        CTARuleTemplate ctaRuleTemplate = ctaRuleTemplateRepository.findOne(id);
        Long userId = UserContext.getUserDetails().getId();
        // While updating rule template, do not update template type
        ctaRuleTemplateDTO.setRuleTemplateType(ctaRuleTemplate.getRuleTemplateType());
        this.buildCTARuleTemplate(ctaRuleTemplate, ctaRuleTemplateDTO, true,countryDTO);
        ctaRuleTemplate.setLastModifiedBy(UserContext.getUserDetails().getId());
        this.save(ctaRuleTemplate);
        return ctaRuleTemplateDTO;
    }


    public Long getExpertiseIdOfCTA(BigInteger ctaId) {
        return costTimeAgreementRepository.getExpertiseOfCTA(ctaId);
    }

    public Long getOrgTypeOfCTA(BigInteger ctaId) {
        return costTimeAgreementRepository.getOrgTypeOfCTA(ctaId);
    }

    public Long getOrgSubTypeOfCTA(BigInteger ctaId) {
        return costTimeAgreementRepository.getOrgSubTypeOfCTA(ctaId);
    }

    public CollectiveTimeAgreementDTO createCopyOfUnitCTA(Long unitId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) throws ExecutionException, InterruptedException {
        logger.info("saving CostTimeAgreement unit {}", unitId);
        if (costTimeAgreementRepository.isCTAExistWithSameNameInUnit(unitId, collectiveTimeAgreementDTO.getName().trim(), new BigInteger("1"))) {
            exceptionService.duplicateDataException("message.cta.name.alreadyExist", collectiveTimeAgreementDTO.getName());

        }
        OrganizationDTO organization = organizationRestClient.getOrganization(unitId);
        CostTimeAgreement costTimeAgreement = new CostTimeAgreement();
        collectiveTimeAgreementDTO.setId(null);
        BeanUtils.copyProperties(collectiveTimeAgreementDTO, costTimeAgreement);
        buildCTA(costTimeAgreement, collectiveTimeAgreementDTO);
        costTimeAgreement.setOrganization(new Organization(organization.getId(),organization.getName(),organization.getDescription()));
        this.save(costTimeAgreement);
        collectiveTimeAgreementDTO.setId(costTimeAgreement.getId());
        return collectiveTimeAgreementDTO;
    }

    public List<CTAResponseDTO> getAllCTAByOrganizationSubType(Long organizationSubTypeId) {
        return costTimeAgreementRepository.getAllCTAByOrganizationSubType(organizationSubTypeId);
    }

    public CollectiveTimeAgreementDTO setCTAWithOrganizationType(Long countryId, BigInteger ctaId, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO, long organizationSubTypeId, boolean checked) {
        if (checked) {
            Integer lastSuffixNumber = costTimeAgreementRepository.getLastSuffixNumberOfCTAName("(?i)" + collectiveTimeAgreementDTO.getName());
            String name = collectiveTimeAgreementDTO.getName();
            collectiveTimeAgreementDTO.setName(name.contains("-") ? name.replace(name.substring(name.lastIndexOf("-") + 1, name.length()), (++lastSuffixNumber).toString()) : collectiveTimeAgreementDTO.getName() + "-" + ++lastSuffixNumber);
            collectiveTimeAgreementDTO.setOrganizationSubType(organizationSubTypeId);
            return countryCTAService.createCostTimeAgreementInCountry(countryId, collectiveTimeAgreementDTO);
        } else {
            Optional<CostTimeAgreement> cta = costTimeAgreementRepository.findById(ctaId);
            if (!cta.isPresent()) {
                exceptionService.dataNotFoundByIdException("message.cta.id.notFound", ctaId);

            }
            CostTimeAgreement costTimeAgreement = cta.get();
            costTimeAgreement.setDeleted(true);
            save(costTimeAgreement);
        }
        return null;
    }


    private Boolean copyRules(CostTimeAgreement costTimeAgreement, CollectiveTimeAgreementDTO collectiveTimeAgreementDTO) {
        List<CTARuleTemplate> ctaRuleTemplates = new ArrayList<>(collectiveTimeAgreementDTO.getRuleTemplates().size());
        for (CTARuleTemplateDTO ctaRuleTemplateDTO : collectiveTimeAgreementDTO.getRuleTemplates()) {
            CTARuleTemplate ctaRuleTemplate = ObjectMapperUtils.copyPropertiesByMapper(ctaRuleTemplateDTO, CTARuleTemplate.class);
            setActivityBasesCostCalculationSettings(ctaRuleTemplate);
            if (ctaRuleTemplateDTO.getRuleTemplateCategory() != null) {
                ctaRuleTemplate.setRuleTemplateCategoryId(ctaRuleTemplateDTO.getRuleTemplateCategory());
            }
            ctaRuleTemplate.setEmploymentTypes(ctaRuleTemplateDTO.getEmploymentTypes());
            ctaRuleTemplates.add(ctaRuleTemplate);
        }
        save(ctaRuleTemplates);
        List<BigInteger> ctaRuleTemplateIds = ctaRuleTemplates.stream().map(c->c.getId()).collect(Collectors.toList());
        costTimeAgreement.setRuleTemplateIds(ctaRuleTemplateIds);
        costTimeAgreement.setStartDateMillis(collectiveTimeAgreementDTO.getStartDateMillis());
        costTimeAgreement.setEndDateMillis(collectiveTimeAgreementDTO.getEndDateMillis());
        return true;
    }
}


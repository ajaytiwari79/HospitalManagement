package com.kairos.cta;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.cta.*;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.Currency;
import com.kairos.persistence.model.user.country.DayType;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionQueryResult;
import com.kairos.response.dto.web.cta.CTARuleTemplateCategoryWrapper;
import com.kairos.response.dto.web.cta.CollectiveTimeAgreementDTO;
import com.kairos.service.agreement.cta.CostTimeAgreementService;
import com.kairos.service.agreement.RuleTemplateCategoryService;
import com.kairos.service.country.CountryService;
import com.kairos.service.country.CurrencyService;
import com.kairos.service.country.DayTypeService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.unit_position.UnitPositionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CostTimeAgreementServiceTest {
    private Logger logger = LoggerFactory.getLogger(CostTimeAgreementService.class);
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private CostTimeAgreementService costTimeAgreementService;
    @Inject
    private CurrencyService currencyService;
    @Inject
    DayTypeService dayTypeService;
    @Inject
    ExpertiseService expertiseService;
    @Inject
    OrganizationService organizationService;
    @Inject
    CountryService countryService;
    @Inject
    UnitPositionService unitPositionService;
    @Value("${server.host.http.url}")
    private String url;
    @Inject
    TestRestTemplate restTemplate;
    static Long createdCtaId = null;
    //    static Long countryId = 53L;
    static Long countryId = null;
    static Long organizationId = null;
    static Long unitPositionId = null;
    static CostTimeAgreement ctaLinkedWithUnitPosition;
    static String nameOfCTA = "Overtime CTA";

    @Before
    public void setUp() throws Exception {

        // Fetch country
        Country country = countryService.getCountryByName("Denmark");
        countryId = country == null ? null : country.getId();

        // Fetch parent unit
        Organization org = organizationService.getOneParentUnitByCountry(countryId);
        organizationId = 163L;//org == null ? null : org.getId();

        // Fetch unit position
        UnitPosition unitPosition = unitPositionService.getDefaultUnitPositionByOrg(organizationId);
        unitPositionId = unitPosition == null ? null : unitPosition.getId();

        ctaLinkedWithUnitPosition = costTimeAgreementService.getCTALinkedWithUnitPosition(unitPositionId);
    }


    @Test
    public void addCTARuleTemplateCategory() {
        RuleTemplateCategory category = new RuleTemplateCategory();
        category.setName("NONE");
        category.setRuleTemplateCategoryType(RuleTemplateCategoryType.CTA);
        ruleTemplateCategoryService.createRuleTemplateCategory(countryId, category);
    }

    @Test
    public void addCTARuleTemplate() {
        CTARuleTemplateDTO ctaRuleTemplateDTO = new CTARuleTemplateDTO("Overtime CTA",
                "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                        " 50% overtime compensation”.",
                "230:50% overtime compensation", "xyz");

        ctaRuleTemplateDTO.setCalculationUnit(CalculationUnit.HOURS);
        CompensationTable compensationTable = new CompensationTable(10);
        ctaRuleTemplateDTO.setCompensationTable(compensationTable);
        // Get currency
        Currency currency = currencyService.getCurrencyByCountryId(countryId);
        FixedValue fixedValue = new FixedValue(10, currency, FixedValue.Type.PER_ACTIVITY);
        ctaRuleTemplateDTO.setCalculateValueAgainst(new CalculateValueAgainst(CalculateValueAgainst.CalculateValueType.FIXED_VALUE, 10.5f, fixedValue));
        ctaRuleTemplateDTO.setApprovalWorkFlow(ApprovalWorkFlow.NO_APPROVAL_NEEDED);
        ctaRuleTemplateDTO.setBudgetType(BudgetType.ACTIVITY_COST);
        ctaRuleTemplateDTO.setActivityTypeForCostCalculation(ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE);
        ctaRuleTemplateDTO.setPlanningCategory(PlanningCategory.DEVIATION_FROM_PLANNED);

        // ctaRuleTemplateDTO.setStaffFunctions(Stream.of(StaffFunction.TRAINING_COORDINATOR).collect(Collectors.toList()));

        //ctaRuleTemplateDTO.setStaffFunctions(Stream.of(StaffFunction.TRAINING_COORDINATOR).collect(Collectors.toList()));

        ctaRuleTemplateDTO.setPlannedTimeWithFactor(PlannedTimeWithFactor.buildPlannedTimeWithFactor(10, true, AccountType.DUTYTIME_ACCOUNT));

        RuleTemplateCategory category = ruleTemplateCategoryService.getCTARuleTemplateCategoryOfCountryByName(countryId, "NONE");
        ctaRuleTemplateDTO.setRuleTemplateCategory(category.getId());
        String baseUrl = getBaseUrl(organizationId, countryId);
        HttpEntity<CTARuleTemplateDTO> requestBodyData = new HttpEntity<>(ctaRuleTemplateDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<CTARuleTemplateDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTARuleTemplateDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<CTARuleTemplateDTO>> response = restTemplate.exchange(
                baseUrl + "/cta_rule_template",
                HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) || HttpStatus.UNPROCESSABLE_ENTITY.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()));

    }


    @Test
    public void getAllRuleTemplate() {
        CTARuleTemplateCategoryWrapper ctaRuleTemplateDTOS = costTimeAgreementService.loadAllCTARuleTemplateByCountry(countryId);
        System.out.println(ctaRuleTemplateDTOS);
    }

    @Test
    public void getCurrency() {

        Currency currency = currencyService.getCurrencyByCountryId(countryId);
        System.out.println(currency);

    }


    @Test
    public void saveCom() {
        costTimeAgreementService.saveInterval();
    }

    @Test
    public void createCostTimeAgreement() {
        CTARuleTemplateDTO ctaRuleTemplateDTO = new CTARuleTemplateDTO("working overtime",
                "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                        " 50% overtime compensation”.",
                "230:50% overtime compensation", "xyz");
        List<CTARuleTemplateDTO> ctaRuleTemplates = new ArrayList<>();
        ctaRuleTemplateDTO = prepareCTARuleTemplate(ctaRuleTemplateDTO);
        ctaRuleTemplates.add(ctaRuleTemplateDTO);
        Expertise expertise = expertiseService.getExpertiseByCountryId(countryId);
        OrganizationType organizationType = organizationService.getOneDefaultOrganizationTypeByCountryId(countryId);
        OrganizationType organizationSubType = organizationService.getOrganizationSubTypeById(organizationType.getId()).get(0);
        CollectiveTimeAgreementDTO collectiveTimeAgreementDTO = new CollectiveTimeAgreementDTO
                (nameOfCTA, "Test description", expertise.getId(), organizationType.getId(), organizationSubType.getId(), new Date().getTime(), ctaRuleTemplates);
        String baseUrl = getBaseUrl(organizationId, countryId);
        HttpEntity<CollectiveTimeAgreementDTO> requestBodyData = new HttpEntity<>(collectiveTimeAgreementDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>> response = restTemplate.exchange(
                baseUrl + "/cta",
                HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.UNPROCESSABLE_ENTITY.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()));
        if (HttpStatus.CREATED.equals(response.getStatusCode())) {
            createdCtaId = response.getBody().getData().getId();
        }
    }


    @Test
    public void updateCostTimeAgreement() {
        // Check for CTA by Id
        if (createdCtaId == null) {
            logger.info("CTA Id is null");
            createdCtaId = costTimeAgreementService.getCTAIdByNameAndCountry(nameOfCTA, countryId);
        }
        // If CTA not found by Id check for cta by name
        if (createdCtaId == null) {
            logger.info("CTA not found with name : {}", nameOfCTA);
            return;
        }
        CTARuleTemplateDTO ctaRuleTemplateDTO = new CTARuleTemplateDTO("Working Overtime",
                "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                        " 50% overtime compensation”.",
                "230:50% overtime compensation", "xyz");
        ctaRuleTemplateDTO = prepareCTARuleTemplate(ctaRuleTemplateDTO);
        List<CTARuleTemplateDTO> ctaRuleTemplates = new ArrayList<>();
        ctaRuleTemplates.add(ctaRuleTemplateDTO);
        Expertise expertise = expertiseService.getExpertiseByCountryId(countryId);
        OrganizationType organizationType = organizationService.getOrganizationTypeByCountryId(countryId).get(0);
        OrganizationType organizationSubType = organizationService.getOrganizationSubTypeById(organizationType.getId()).get(0);
        CollectiveTimeAgreementDTO collectiveTimeAgreementDTO = new CollectiveTimeAgreementDTO
                (nameOfCTA, "Test description", expertise.getId(), organizationType.getId(),
                        organizationSubType.getId(), new Date().getTime(), ctaRuleTemplates);

        collectiveTimeAgreementDTO.setDescription("Description updated");

        String baseUrl = getBaseUrl(71L, 53L);
        HttpEntity<CollectiveTimeAgreementDTO> requestBodyData = new HttpEntity<>(collectiveTimeAgreementDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>> response = restTemplate.exchange(
                baseUrl + "/cta/" + createdCtaId,
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdCtaId = response.getBody().getData().getId();
    }

    @Test
    @Ignore
    public void changeCTARuleTemplateCategory() throws Exception {
        CTARuleTemplateCategoryWrapper ctaRuleTemplateDTOS = costTimeAgreementService.loadAllCTARuleTemplateByCountry(53L);
        ArrayList arrayList = new ArrayList();
        List<Long> ctaRuleTemplateList = null;
        ctaRuleTemplateList.add(ctaRuleTemplateDTOS.getRuleTemplates().get(0).getId());
        String ruleTemplateCategory = "test";
    }

    @Test
    public void updateCTARuleTemplateCategory() throws Exception {
        CTARuleTemplateDTO ctaRuleTemplateDTO = new CTARuleTemplateDTO("Working Overtime",
                "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                        " 50% overtime compensation”.",
                "230:50% overtime compensation", "xyz");
        List<CTARuleTemplateDTO> ctaRuleTemplates = new ArrayList<>();
        ctaRuleTemplates.add(ctaRuleTemplateDTO);
        Expertise expertise = expertiseService.getExpertiseByCountryId(countryId);
        OrganizationType organizationType = organizationService.getOrganizationTypeByCountryId(countryId).get(0);
        OrganizationType organizationSubType = organizationService.getOrganizationSubTypeById(organizationType.getId()).get(0);
        CollectiveTimeAgreementDTO collectiveTimeAgreementDTO = new CollectiveTimeAgreementDTO
                ("CTA TEST", "Test description", expertise.getId(), organizationType.getId(),
                        organizationSubType.getId(), new Date().getTime(), ctaRuleTemplates);

        if (createdCtaId == null) {
            // GET CTA with name  "New Test CTA"
            try {
                collectiveTimeAgreementDTO = costTimeAgreementService.createCostTimeAgreement(countryId, collectiveTimeAgreementDTO);
            } catch (Exception e) {
                logger.info("Exception occured");
            }
            createdCtaId = collectiveTimeAgreementDTO.getId();
        }
        collectiveTimeAgreementDTO.setDescription("Description updated");

        String baseUrl = getBaseUrl(71L, 53L);
        HttpEntity<CollectiveTimeAgreementDTO> requestBodyData = new HttpEntity<>(collectiveTimeAgreementDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>> response = restTemplate.exchange(
                baseUrl + "/cta/" + createdCtaId,
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdCtaId = response.getBody().getData().getId();

    }

    public CTARuleTemplateDTO prepareCTARuleTemplate(CTARuleTemplateDTO ctaRuleTemplateDTO) {
//        ctaRuleTemplateDTO

        // Prepare Compensation Table
        CompensationTableInterval compensationTableInterval = new CompensationTableInterval(LocalTime.MIN, LocalTime.MAX,
                10, CompensationMeasurementType.MINUTES);
        List<CompensationTableInterval> compensationTableIntervals = new ArrayList<>();
        compensationTableIntervals.add(compensationTableInterval);
        CompensationTable compensationTable = new CompensationTable(10, compensationTableIntervals);
        ctaRuleTemplateDTO.setCompensationTable(compensationTable);

        // Prepare Activity Data
        ctaRuleTemplateDTO.setActivityTypeForCostCalculation(ActivityTypeForCostCalculation.SELECTED_ACTIVITY_TYPE);
        return ctaRuleTemplateDTO;
    }


    @Test
    public void createCostTimeAgreementForUnitPosition() {
        // Check for CTA by Id
        if (ctaLinkedWithUnitPosition == null) {
            logger.info("CTA With Unit position not found");
            return;
        }

        CTARuleTemplateDTO ctaRuleTemplateDTO = new CTARuleTemplateDTO("Working Overtime",
                "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                        " 50% overtime compensation”.",
                "230:50% overtime compensation", "xyz");
        ctaRuleTemplateDTO = prepareCTARuleTemplate(ctaRuleTemplateDTO);
        List<CTARuleTemplateDTO> ctaRuleTemplates = new ArrayList<>();
        ctaRuleTemplates.add(ctaRuleTemplateDTO);

        Long expertiseId = costTimeAgreementService.getExpertiseIdOfCTA(ctaLinkedWithUnitPosition.getId());
        Long orgTypeId = costTimeAgreementService.getOrgTypeOfCTA(ctaLinkedWithUnitPosition.getId());
        Long orgSubTypeId = costTimeAgreementService.getOrgSubTypeOfCTA(ctaLinkedWithUnitPosition.getId());

        CollectiveTimeAgreementDTO collectiveTimeAgreementDTO = new CollectiveTimeAgreementDTO
                (nameOfCTA, "Test description", expertiseId, orgTypeId,
                        orgSubTypeId, new Date().getTime(), ctaRuleTemplates);

        collectiveTimeAgreementDTO.setDescription("Description updated");

        String baseUrl = getBaseUrl(organizationId, null);
        HttpEntity<CollectiveTimeAgreementDTO> requestBodyData = new HttpEntity<>(collectiveTimeAgreementDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitPositionQueryResult>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitPositionQueryResult>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<UnitPositionQueryResult>> response = restTemplate.exchange(
                baseUrl + "/unit/" + organizationId + "/unit_position/" + unitPositionId + "/cta/" + ctaLinkedWithUnitPosition.getId(),
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getCTALinkedWithUnitPosition() throws Exception {

        String baseUrl = getBaseUrl(organizationId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAListQueryResult>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAListQueryResult>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<CTAListQueryResult>> response = restTemplate.exchange(
                baseUrl + "/unit/" + organizationId + "/unit_position/" + unitPositionId + "/cta",
                HttpMethod.GET, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}", response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()));

    }

    public final String getBaseUrl(Long organizationId, Long countryId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }

}

package com.kairos.cta;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.cta.*;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.Currency;
import com.kairos.persistence.model.user.country.DayType;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.response.dto.web.cta.CTARuleTemplateCategoryWrapper;
import com.kairos.response.dto.web.cta.CollectiveTimeAgreementDTO;
import com.kairos.service.agreement.cta.CostTimeAgreementService;
import com.kairos.service.agreement.RuleTemplateCategoryService;
import com.kairos.service.country.CountryService;
import com.kairos.service.country.CurrencyService;
import com.kairos.service.country.DayTypeService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.organization.OrganizationService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CostTimeAgreementServiceTest {
    private Logger logger = LoggerFactory.getLogger(CostTimeAgreementService.class);
    @Autowired private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Autowired private CostTimeAgreementService costTimeAgreementService;
    @Autowired private CurrencyService currencyService;
    @Autowired DayTypeService dayTypeService;
    @Autowired ExpertiseService expertiseService;
    @Autowired OrganizationService organizationService;
    @Autowired CountryService countryService;
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    static Long createdCtaId = null;
//    static Long countryId = 53L;
    static Long countryId = null;
    static Long organizationId = null;

    @Before
    public void setUp() throws Exception {
        Country country = countryService.getCountryByName("Denmark");
        countryId = country == null ? null : country.getId();
        organizationId = 145l;
    }


    @Test
    public void addCTARuleTemplateCategory(){
        RuleTemplateCategory category=new RuleTemplateCategory();
        category.setName("NONE");
        category.setRuleTemplateCategoryType(RuleTemplateCategoryType.CTA);
        ruleTemplateCategoryService.createRuleTemplateCategory(53L,category);
    }

    @Test
    public void addCTARuleTemplate()
    {
        costTimeAgreementService.createDefaultCtaRuleTemplate(53L);
    }

    @Test
    public void getHoliday(){
     Date date= Date.from(LocalDate.of(2018,1,2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        List<DayType> dayTypes= dayTypeService.getDayTypeByDate(53L,date);
        System.out.println(dayTypes);
    }

    @Test
    public void getAllRuleTemplate(){
        CTARuleTemplateCategoryWrapper ctaRuleTemplateDTOS= costTimeAgreementService.loadAllCTARuleTemplateByCountry(53L);
        System.out.println(ctaRuleTemplateDTOS);
    }
    @Test
    public void getCurrency(){

        Currency currency=currencyService.getCurrencyByCountryId(53L);
        System.out.println(currency);

    }


    @Test
    public void saveCom(){
        costTimeAgreementService.saveInterval();
    }

    @Test
    public void createCostTimeAgreement(){
        CTARuleTemplateDTO ctaRuleTemplateDTO  = new CTARuleTemplateDTO("New Test CTA",
                "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                        " 50% overtime compensation”.", CTARuleTemplateType.RULE_TEMPLATE_7,
                "230:50% overtime compensation", "xyz");
        List<CTARuleTemplateDTO> ctaRuleTemplates = new ArrayList<>();
        ctaRuleTemplateDTO = prepareCTARuleTemplate(ctaRuleTemplateDTO);
        ctaRuleTemplates.add(ctaRuleTemplateDTO);
        Expertise expertise = expertiseService.getExpertiseByCountryId(countryId);
        OrganizationType organizationType = organizationService.getOneDefaultOrganizationTypeByCountryId(countryId);
        OrganizationType organizationSubType = organizationService.getOrganizationSubTypeById(organizationType.getId()).get(0);
        CollectiveTimeAgreementDTO collectiveTimeAgreementDTO = new CollectiveTimeAgreementDTO
                ("CTA TEST", "Test description", expertise.getId(), organizationType.getId(), organizationSubType.getId(), new Date().getTime(), ctaRuleTemplates);
        String baseUrl = getBaseUrl(organizationId, countryId);
        HttpEntity<CollectiveTimeAgreementDTO> requestBodyData = new HttpEntity<>(collectiveTimeAgreementDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<CollectiveTimeAgreementDTO>> response = restTemplate.exchange(
                baseUrl + "/cta",
                HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : "+response.getStatusCode());
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) ||  HttpStatus.UNPROCESSABLE_ENTITY.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()));
        if(HttpStatus.CREATED.equals(response.getStatusCode())) {
            createdCtaId = response.getBody().getData().getId();
        }
    }


    @Test
    public void updateCostTimeAgreement(){
        CTARuleTemplateDTO ctaRuleTemplateDTO  = new CTARuleTemplateDTO("Working Overtime",
                "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                        " 50% overtime compensation”.", CTARuleTemplateType.RULE_TEMPLATE_7,
                "230:50% overtime compensation", "xyz");
        List<CTARuleTemplateDTO> ctaRuleTemplates = new ArrayList<>();
        ctaRuleTemplates.add(ctaRuleTemplateDTO);
        Expertise expertise = expertiseService.getExpertiseByCountryId(countryId);
        OrganizationType organizationType = organizationService.getOrganizationTypeByCountryId(countryId).get(0);
        OrganizationType organizationSubType = organizationService.getOrganizationSubTypeById(organizationType.getId()).get(0);
        CollectiveTimeAgreementDTO collectiveTimeAgreementDTO = new CollectiveTimeAgreementDTO
                ("CTA TEST", "Test description", expertise.getId(), organizationType.getId(),
                        organizationSubType.getId(), new Date().getTime(), ctaRuleTemplates);

        if(createdCtaId == null){
            // GET CTA with name  "New Test CTA"
            try{
                collectiveTimeAgreementDTO = costTimeAgreementService.createCostTimeAgreement(countryId, collectiveTimeAgreementDTO);
            } catch (Exception e){
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
                baseUrl + "/cta/"+createdCtaId,
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdCtaId=  response.getBody().getData().getId();
    }

    @Test
    @Ignore
    public  void changeCTARuleTemplateCategory() throws Exception{
        CTARuleTemplateCategoryWrapper ctaRuleTemplateDTOS= costTimeAgreementService.loadAllCTARuleTemplateByCountry(53L);
        ArrayList arrayList = new ArrayList();
         List<Long> ctaRuleTemplateList=null;
        ctaRuleTemplateList.add(ctaRuleTemplateDTOS.getRuleTemplates().get(0).getId());
        String ruleTemplateCategory="test";
    }

    public  void updateCTARuleTemplateCategory() throws Exception{
        CTARuleTemplateDTO ctaRuleTemplateDTO  = new CTARuleTemplateDTO("Working Overtime",
                "CTA rule for overtime shift, from 00-24 o. clock.  For this organization/unit this is payroll type “230: " +
                        " 50% overtime compensation”.", CTARuleTemplateType.RULE_TEMPLATE_7,
                "230:50% overtime compensation", "xyz");
        List<CTARuleTemplateDTO> ctaRuleTemplates = new ArrayList<>();
        ctaRuleTemplates.add(ctaRuleTemplateDTO);
        Expertise expertise = expertiseService.getExpertiseByCountryId(countryId);
        OrganizationType organizationType = organizationService.getOrganizationTypeByCountryId(countryId).get(0);
        OrganizationType organizationSubType = organizationService.getOrganizationSubTypeById(organizationType.getId()).get(0);
        CollectiveTimeAgreementDTO collectiveTimeAgreementDTO = new CollectiveTimeAgreementDTO
                ("CTA TEST", "Test description", expertise.getId(), organizationType.getId(),
                        organizationSubType.getId(), new Date().getTime(), ctaRuleTemplates);

        if(createdCtaId == null){
            // GET CTA with name  "New Test CTA"
            try{
                collectiveTimeAgreementDTO = costTimeAgreementService.createCostTimeAgreement(countryId, collectiveTimeAgreementDTO);
            } catch (Exception e){
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
                baseUrl + "/cta/"+createdCtaId,
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdCtaId=  response.getBody().getData().getId();

    }

    public CTARuleTemplateDTO prepareCTARuleTemplate(CTARuleTemplateDTO ctaRuleTemplateDTO){
//        ctaRuleTemplateDTO

        // Prepare Compensation Table
        CompensationTableInterval compensationTableInterval = new CompensationTableInterval(LocalTime.MIN, LocalTime.MAX,
                10,CompensationMeasurementType.MINUTES);
        List<CompensationTableInterval> compensationTableIntervals = new ArrayList<>();
        compensationTableIntervals.add(compensationTableInterval);
        CompensationTable compensationTable = new CompensationTable(10, compensationTableIntervals);
        ctaRuleTemplateDTO.setCompensationTable(compensationTable);


        return ctaRuleTemplateDTO;
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

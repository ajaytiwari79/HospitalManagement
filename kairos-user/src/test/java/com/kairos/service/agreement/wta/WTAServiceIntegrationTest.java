package com.kairos.service.agreement.wta;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.util.DateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.kairos.constants.AppConstants.*;

/**
 * Created by vipul on 2/1/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WTAServiceIntegrationTest {
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;

    static WTADTO wtadto = new WTADTO();
    static Long currentMillis = System.currentTimeMillis();
    static Long createdId, createdIdDelete;
    static RuleTemplateCategoryDTO maximumShiftLengthWTATemplate;
    static RuleTemplateCategoryDTO minimumShiftLengthWTATemplate;
    static RuleTemplateCategoryDTO numberOfWeekendShiftInPeriodWTATemplate;
    static RuleTemplateCategoryDTO minimumDailyRestingTimeWTATemplate;
    static RuleTemplateCategoryDTO careDayCheckWTATemplate;
    static List<RuleTemplateCategoryDTO> baseRuleTemplates = new ArrayList<>(20);
    static String baseUrlWithCountry;
    static String baseUrlWithUnit;
    private final Logger logger = LoggerFactory.getLogger(WTAServiceIntegrationTest.class);

    @Before
    public void setUp() throws Exception {
        String MONTHS = "MONTHS";
        String TUESDAY = "TUESDAY";
        long timeInMins = 10;
        long daysCount = 10;
        long dateInMillis = DateUtil.getCurrentDate().getTime();
        List<String> balanceTypes = new ArrayList<>(0);

        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory("NONE", RuleTemplateCategoryType.WTA);

        maximumShiftLengthWTATemplate = new RuleTemplateCategoryDTO(TEMPLATE1_NAME, TEMPLATE1, false, TEMPLATE1_DESCRIPTION, timeInMins, balanceTypes, true);
        minimumShiftLengthWTATemplate = new RuleTemplateCategoryDTO(TEMPLATE2_NAME, TEMPLATE2, false, TEMPLATE2_DESCRIPTION, timeInMins, balanceTypes, true);
        numberOfWeekendShiftInPeriodWTATemplate = new RuleTemplateCategoryDTO(TEMPLATE13_NAME, TEMPLATE13, true, TEMPLATE13_DESCRIPTION, 12L, 12L, TUESDAY, 2L, true, TUESDAY, 1L);
        careDayCheckWTATemplate = new RuleTemplateCategoryDTO(TEMPLATE14_NAME, TEMPLATE14, true, TEMPLATE14_DESCRIPTION, 2L, dateInMillis, MONTHS, 1L);
        minimumDailyRestingTimeWTATemplate = new RuleTemplateCategoryDTO(TEMPLATE15_NAME, TEMPLATE15, true, TEMPLATE15_DESCRIPTION, timeInMins);

        maximumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
        minimumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
        numberOfWeekendShiftInPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
        careDayCheckWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
        minimumDailyRestingTimeWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);

        baseRuleTemplates.add(maximumShiftLengthWTATemplate);
        baseRuleTemplates.add(minimumShiftLengthWTATemplate);
        wtadto.setName("Hello" + new Date());
        wtadto.setDescription("test");
        wtadto.setStartDateMillis(currentMillis);
        wtadto.setEndDateMillis(currentMillis + (24 * 60 * 60 * 60 * 1000));
        wtadto.setOrganizationType(86L);
        wtadto.setOrganizationSubType(93L);
        wtadto.setRuleTemplates(Collections.emptyList());
        wtadto.setTags(Collections.emptyList());
        wtadto.setExpertiseId(6959L);
        wtadto.setRuleTemplates(baseRuleTemplates);
        baseUrlWithCountry = getBaseUrl(71L, 53L, null);
        baseUrlWithUnit = getBaseUrl(71L, null, 95L);
        logger.info(wtadto.toString());
    }


    @Test
    public void test1_createWta() throws Exception {
        HttpEntity<WTADTO> requestBodyData = new HttpEntity<>(wtadto);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<WorkingTimeAgreement>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<WorkingTimeAgreement>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<WorkingTimeAgreement>> response = restTemplate.exchange(
                baseUrlWithCountry + "/wta",
                HttpMethod.POST, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
        createdId = createdIdDelete = response.getBody().getData().getId();
    }

    @Test
    public void test2_getWtaForCountry() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAWithCountryAndOrganizationTypeDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAWithCountryAndOrganizationTypeDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<WTAWithCountryAndOrganizationTypeDTO>>> response = restTemplate.exchange(
                baseUrlWithCountry + "/wta/ByCountry",
                HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void test3_getWtaForOrganization() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAWithCountryAndOrganizationTypeDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAWithCountryAndOrganizationTypeDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<WTAWithCountryAndOrganizationTypeDTO>>> response = restTemplate.exchange(
                baseUrlWithUnit + "/wta/ByOrganization",
                HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    //updateWtaOfCountry
    @Test
    public void test4_updateWtaForOrganization() throws Exception {

        wtadto.setDescription("Its updating description of wta of Organization");
        List<RuleTemplateCategoryDTO> baseRuleTemplates = new ArrayList<>(20);
        baseRuleTemplates.add(careDayCheckWTATemplate);
        currentMillis = System.currentTimeMillis();
        wtadto.setStartDateMillis(currentMillis + (2 * 12 * 60 * 60 * 60 * 1000));
        wtadto.setEndDateMillis(currentMillis + (4 * 24 * 60 * 60 * 60 * 1000));// adding 4 days millie to it
        wtadto.setRuleTemplates(baseRuleTemplates);
        logger.info("currentTime {}. startDate {} , endDate {} ", currentMillis, wtadto.getStartDateMillis(), wtadto.getEndDateMillis());
        HttpEntity<WTADTO> requestBodyData = new HttpEntity<>(wtadto);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<WorkingTimeAgreement>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<WorkingTimeAgreement>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<WorkingTimeAgreement>> response = restTemplate.exchange(
                baseUrlWithUnit + "/wta/14640",
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void test5_updateWtaOfCountry() throws Exception {
        wtadto.setDescription("Its updating description of wta of Country");
        List<RuleTemplateCategoryDTO> baseRuleTemplates = new ArrayList<>(20);
        baseRuleTemplates.add(minimumDailyRestingTimeWTATemplate);
        currentMillis = System.currentTimeMillis();
        wtadto.setStartDateMillis(currentMillis + (2 * 12 * 60 * 60 * 60 * 1000));
        wtadto.setEndDateMillis(currentMillis + (4 * 24 * 60 * 60 * 60 * 1000));// adding 4 days millie to it
        wtadto.setRuleTemplates(baseRuleTemplates);
        HttpEntity<WTADTO> requestBodyData = new HttpEntity<>(wtadto);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<WorkingTimeAgreement>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<WorkingTimeAgreement>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<WorkingTimeAgreement>> response = restTemplate.exchange(
                baseUrlWithCountry + "/wta/14658",
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }


    public final String getBaseUrl(Long organizationId, Long countryId, Long unitId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null && unitId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }
    }

}
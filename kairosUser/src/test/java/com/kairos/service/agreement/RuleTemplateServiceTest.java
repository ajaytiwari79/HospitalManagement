package com.kairos.service.agreement;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.persistence.model.user.agreement.wta.templates.PhaseTemplateValue;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.service.exception.ExceptionService;
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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by pavan on 28/2/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RuleTemplateServiceTest {


    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    RuleTemplateService ruleTemplateService;
    @Inject
    private ExceptionService exceptionService;
    static RuleTemplateCategoryDTO ruleTemplateCategoryDTO=new RuleTemplateCategoryDTO();
    private final Logger logger = LoggerFactory.getLogger(RuleTemplateServiceTest.class);
    static String baseUrlWithCountry;
    static Long ruleTemplateIdForUpdate, createdIdDelete;

    @Before
    public void setUp() throws Exception {
        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory("test-cat", RuleTemplateCategoryType.WTA);
        List<PhaseTemplateValue> phaseTemplateValues=new ArrayList<>(4);
        PhaseTemplateValue list1=new PhaseTemplateValue(1,"REQUEST",(short)10,(short)20,true,6,false);
        PhaseTemplateValue list2=new PhaseTemplateValue(2,"PUZZLE",(short)20,(short)20,true,7,false);
        PhaseTemplateValue list3=new PhaseTemplateValue(3,"CONSTRUCTION",(short)30,(short)20,true,8,false);
        PhaseTemplateValue list4=new PhaseTemplateValue(4,"DRAFT",(short)40,(short)20,true,9,false);
        phaseTemplateValues.add(list1);
        phaseTemplateValues.add(list2);
        phaseTemplateValues.add(list3);
        phaseTemplateValues.add(list4);
        ruleTemplateCategoryDTO.setName("Demo copy of minimum-daily-resting-time");
        ruleTemplateCategoryDTO.setTemplateType("minimum-daily-resting-time-3");
        ruleTemplateCategoryDTO.setDescription("Rule Template copy test");
        ruleTemplateCategoryDTO.setRuleTemplateCategory(ruleTemplateCategory);
        ruleTemplateCategoryDTO.setDisabled(true);
        ruleTemplateCategoryDTO.setCheckAgainstTimeRules(true);
        ruleTemplateCategoryDTO.setPhaseTemplateValues(phaseTemplateValues);
        ruleTemplateCategoryDTO.setContinuousDayRestHours(156L);
        ruleTemplateCategoryDTO.setRecommendedValue(20);
        ruleTemplateCategoryDTO.setContinuousDayRestHours(50L);
        ruleTemplateCategoryDTO.setId(4296L);

        baseUrlWithCountry = getBaseUrl(24L, 4L, null);

    }
    @Test
    public void copyRuleTemplate() throws Exception {
        HttpEntity<RuleTemplateCategoryDTO> requestBodyData = new HttpEntity<>(ruleTemplateCategoryDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Map>> response = restTemplate.exchange(
                baseUrlWithCountry + "/copy_rule_template",
                HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code:"+response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    }

    @Test
    public void getRuleTemplate() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Map>> response = restTemplate.exchange(
                baseUrlWithCountry + "/rule_templates",
                HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        if (response.getBody().getData().size() > 0) {
            logger.info("Returned data:"+response.getBody().getData());
        }
    }

    @Test
    public void updateRuleTemplate(){

        ruleTemplateCategoryDTO.setDescription("Its updating description of rule template of shortest-and-average-daily-rest-fixed-times-1");
        ruleTemplateCategoryDTO.setTemplateType("minimum-daily-resting-time");
        ruleTemplateCategoryDTO.setName("Demo copy of minimum-daily-resting-time");
        ruleTemplateCategoryDTO.setDescription("Rule Template copy test");
        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory("test-cat", RuleTemplateCategoryType.WTA);
        ruleTemplateCategoryDTO.setRuleTemplateCategory(ruleTemplateCategory);
        ruleTemplateCategoryDTO.setDisabled(true);
        ruleTemplateCategoryDTO.setContinuousDayRestHours(156L);
        ruleTemplateCategoryDTO.setRecommendedValue(20);
        ruleTemplateCategoryDTO.setId(4296L);

        HttpEntity<RuleTemplateCategoryDTO> requestBodyData = new HttpEntity<RuleTemplateCategoryDTO>(ruleTemplateCategoryDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<RuleTemplateCategoryDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<RuleTemplateCategoryDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<RuleTemplateCategoryDTO>> response = restTemplate.exchange(
                baseUrlWithCountry + "/rule_templates/sdg1",
                HttpMethod.PUT, requestBodyData, typeReference);
        logger.info("status code:"+response.getStatusCode());
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
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }

}
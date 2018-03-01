package com.kairos.service.agreement;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.templates.PhaseTemplateValue;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
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

    static RuleTemplateCategoryDTO ruleTemplateCategoryDTO=new RuleTemplateCategoryDTO();
    private final Logger logger = LoggerFactory.getLogger(RuleTemplateServiceTest.class);
    static String baseUrlWithCountry;
    static Long createdId, createdIdDelete;

    @Before
    public void setUp() throws Exception {
        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory("NONE", RuleTemplateCategoryType.WTA);
        List<PhaseTemplateValue> phaseTemplateValues=new ArrayList<>(4);
        PhaseTemplateValue list1=new PhaseTemplateValue(1,"REQUEST",(short)10,(short)20,true,6,false);
        PhaseTemplateValue list2=new PhaseTemplateValue(1,"PUZZLE",(short)20,(short)20,true,7,false);
        PhaseTemplateValue list3=new PhaseTemplateValue(1,"CONSTRUCTION",(short)30,(short)20,true,8,false);
        PhaseTemplateValue list4=new PhaseTemplateValue(1,"DRAFT",(short)40,(short)20,true,9,false);
        phaseTemplateValues.add(list1);
        phaseTemplateValues.add(list2);
        phaseTemplateValues.add(list3);
        phaseTemplateValues.add(list4);
        ruleTemplateCategoryDTO.setName("Final Test Rule Template test");
        ruleTemplateCategoryDTO.setTemplateType("TEMPLATE43");
        ruleTemplateCategoryDTO.setDescription("Rule Template copy test");
        ruleTemplateCategoryDTO.setRuleTemplateCategory(ruleTemplateCategory);
        ruleTemplateCategoryDTO.setDisabled(true);
        ruleTemplateCategoryDTO.setCheckAgainstTimeRules(true);
        ruleTemplateCategoryDTO.setPhaseTemplateValues(phaseTemplateValues);
        ruleTemplateCategoryDTO.setRecommendedValue(20);
        baseUrlWithCountry = getBaseUrl(24L, 4L, null);

    }
    @Test
    public void copyRuleTemplate() throws Exception {
        HttpEntity<RuleTemplateCategoryDTO> requestBodyData = new HttpEntity<>(ruleTemplateCategoryDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Map>> response = restTemplate.exchange(
                baseUrlWithCountry + "/copy_rule_templates/TEMPLATE54",
                HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code:"+response.getStatusCode());
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
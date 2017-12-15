package com.kairos.service.agreement;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.timetype.PresenceTypeDTO;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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

/**
 * Created by vipul on 14/12/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RuleTemplateCategoryServiceTest {
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    static Long createdId;
    static Long createdIdForDelete;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test1_createRuleTemplateCategory() throws Exception {
        RuleTemplateCategory category=new RuleTemplateCategory();
        category.setName("NONE1");
        category.setRuleTemplateCategoryType(RuleTemplateCategoryType.CTA);
        String baseUrl = getBaseUrl(71L, 53L);
        HttpEntity<RuleTemplateCategory> requestBodyData = new HttpEntity<>(category);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<RuleTemplateCategory>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<RuleTemplateCategory>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<RuleTemplateCategory>> response = restTemplate.exchange(
                baseUrl + "/template_category",
                HttpMethod.POST, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
        createdIdForDelete= createdId = response.getBody().getData().getId();
    }

    @Test
    @Ignore
    public void getRulesTemplateCategory() throws Exception {

    }

    @Test
    @Ignore
    public void exists() throws Exception {

    }

    @Test
    public void test2_deleteRuleTemplateCategory() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        ResponseEntity<PresenceTypeDTO> response = restTemplate.exchange(
                baseUrl + "/template_category/" + createdIdForDelete,
                HttpMethod.DELETE, null, PresenceTypeDTO.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
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
    @Test
    @Ignore
    public void updateRuleTemplateCategory() throws Exception {

    }

    @Test
    @Ignore
    public void setRuleTemplatecategoryWithRuleTemplate() throws Exception {

    }

    @Test
    @Ignore
    public void updateRuleTemplateCategory1() throws Exception {

    }

    @Test
    @Ignore
    public void changeCTARuleTemplateCategory() throws Exception {

    }

}
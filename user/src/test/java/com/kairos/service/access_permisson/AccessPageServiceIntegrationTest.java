package com.kairos.service.access_permisson;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.access_permission.AccessPageDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.service.country.CountryService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.user.access_page.OrgCategoryTabAccessDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prerna on 1/3/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AccessPageServiceIntegrationTest {

    private Logger logger = LoggerFactory.getLogger(AccessPageServiceIntegrationTest.class);

    @Value("${server.host.http.url}")
    private String url ;
    @Inject
    TestRestTemplate restTemplate;

    @Inject private CountryService countryService;
    @Inject private OrganizationService organizationService;
    @Inject private AccessPageService accessPageService;
    @Inject private ExceptionService exceptionService;
    static Long countryId = null;
    static Long organizationId = null;
    static Long mainModuleId = null;
    static String nameOfMainModule = null;
    @Before
    public void setUp() throws Exception {
        // Fetch country
        Country country = countryService.getCountryByName("Denmark");
        countryId = country == null ? null : country.getId();

        // Fetch parent unit
        Organization org = organizationService.getOneParentUnitByCountry(countryId);
        organizationId = org == null ? null : org.getId();

        // Fetch parent module
        AccessPage accessPage = accessPageService.getOneMainModule();
        mainModuleId = accessPage == null ? null : accessPage.getId();
        nameOfMainModule = accessPage == null ? null : accessPage.getName();
    }

    @Test
    @OrderTest(order = 1)
    public void updateAccessForOrganizationCategory() throws Exception {

        logger.info("mainModuleId : {}",mainModuleId);
        String baseUrl=getBaseUrl(organizationId,countryId, null);

        OrgCategoryTabAccessDTO orgCategoryTabAccessDTO = new OrgCategoryTabAccessDTO(OrganizationCategory.HUB, true);

        HttpEntity<OrgCategoryTabAccessDTO> requestBodyData = new HttpEntity<>(orgCategoryTabAccessDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/tab/"+mainModuleId+"/access_status",
                HttpMethod.PUT, requestBodyData, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );

    }


    @Test
    @OrderTest(order = 2)
    public void updateAccessPage() throws Exception {

        logger.info("mainModuleId : {}",mainModuleId);
        String baseUrl=getBaseUrl(organizationId,null, null);

        AccessPageDTO accessPageDTO = new AccessPageDTO();
        accessPageDTO.setName(nameOfMainModule);
        HttpEntity<AccessPageDTO> requestBodyData = new HttpEntity<>(accessPageDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<HashMap<String,Object>>> response = restTemplate.exchange(
                baseUrl+"/tab/"+mainModuleId,
                HttpMethod.PUT, requestBodyData, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );
    }

    @Test
    @OrderTest(order = 3)
    public void getMainTabs() throws Exception {

        String baseUrl=getBaseUrl(organizationId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccessPageDTO>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccessPageDTO>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<List<AccessPageDTO>>> response = restTemplate.exchange(
                baseUrl+"/tab",
                HttpMethod.GET, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

    }

    @Test
    @OrderTest(order = 4)
    public void getChildTabs() throws Exception {

        String baseUrl=getBaseUrl(organizationId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccessPageDTO>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccessPageDTO>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<List<AccessPageDTO>>> response = restTemplate.exchange(
                baseUrl+"/tab/"+mainModuleId+"/tabs",
                HttpMethod.GET, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

    public final String getBaseUrl(Long organizationId,Long countryId, Long unitId){
        if(organizationId!=null && countryId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if(organizationId!=null && unitId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        } else if(organizationId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else{
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
    return null;
    }

}
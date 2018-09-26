package com.kairos.service.system_setting;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.service.country.CountryService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.dto.user.country.system_setting.SystemLanguageDTO;
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
import java.util.List;

@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SystemLanguageServiceIntegrationTest {


    private Logger logger = LoggerFactory.getLogger(SystemLanguageService.class);

    @Value("${server.host.http.url}")
    private String url ;
    @Inject
    TestRestTemplate restTemplate;

    @Inject
    private CountryService countryService;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private SystemLanguageService systemLanguageService;

    static String nameOfSystemLanguage = "English";
    static Long createdSystemLanguageId = null;
    static Long countryId = null;
    static Long organizationId = null;

    @Before
    public void setUp() throws Exception {

        // Fetch country
        Country country = countryService.getCountryByName("Denmark");
        countryId = country == null ? null : country.getId();

        // Fetch parent unit
        Organization org = organizationService.getOneParentUnitByCountry(countryId);
        organizationId = org == null ? null : org.getId();
    }


    @Test
    @OrderTest(order = 1)
    public void addSystemLanguage() {

        String baseUrl=getBaseUrl(organizationId,null, null);
        SystemLanguageDTO systemLanguageDTO = new SystemLanguageDTO(nameOfSystemLanguage, "en", true, true);
        logger.info("System Language name : "+systemLanguageDTO.getName());
        HttpEntity<SystemLanguageDTO> requestBodyData = new HttpEntity<>(systemLanguageDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<SystemLanguageDTO>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<SystemLanguageDTO>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<SystemLanguageDTO>> response = restTemplate.exchange(
                baseUrl+"/system_language",
                HttpMethod.POST, requestBodyData, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
        if(HttpStatus.CREATED.equals(response.getStatusCode())) {
            createdSystemLanguageId = response.getBody().getData().getId();
        }
    }

    @Test
    @OrderTest(order = 2)
    public void updateSystemLanguage() {
        logger.info("System Language name  : "+nameOfSystemLanguage);
        String baseUrl=getBaseUrl(organizationId,null, null);

        if(createdSystemLanguageId == null){
            logger.info("System Language Id is null");
            SystemLanguage systemLanguage = systemLanguageService.getSystemLanguageByName(nameOfSystemLanguage);
            createdSystemLanguageId = systemLanguage.getId();
        }
        SystemLanguageDTO systemLanguageDTO = new SystemLanguageDTO(nameOfSystemLanguage, "en", true, true);


        HttpEntity<SystemLanguageDTO> requestBodyData = new HttpEntity<>(systemLanguageDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<SystemLanguageDTO>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<SystemLanguageDTO>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<SystemLanguageDTO>> response = restTemplate.exchange(
                baseUrl+"/system_language/"+ createdSystemLanguageId,
                HttpMethod.PUT, requestBodyData, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) || HttpStatus.FORBIDDEN.equals(response.getStatusCode()));
    }

    @Test
    @OrderTest(order = 3)
    public void deleteSystemLanguage() {

        if(createdSystemLanguageId == null){
            logger.info("System Language Id is null");
            SystemLanguage systemLanguage = systemLanguageService.getSystemLanguageByName(nameOfSystemLanguage);
            createdSystemLanguageId = systemLanguage.getId();
        }
        logger.info("System Language Id : "+ createdSystemLanguageId);
        String baseUrl=getBaseUrl(organizationId,null, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/system_language/"+ createdSystemLanguageId,
                HttpMethod.DELETE, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

    @Test
    @OrderTest(order = 4)
    public void updateSystemLanguageOfCountry() {

        logger.info("System Language name  : "+nameOfSystemLanguage);
        String baseUrl=getBaseUrl(organizationId,countryId, null);

        if(createdSystemLanguageId == null){
            logger.info("System Language Id is null");
            SystemLanguage systemLanguage = systemLanguageService.getSystemLanguageByName(nameOfSystemLanguage);
            createdSystemLanguageId = systemLanguage.getId();
        }

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/system_language/"+ createdSystemLanguageId,
                HttpMethod.PUT,null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );
    }

    @Test
    @OrderTest(order = 5)
    public void getListOfSystemLanguage() {

        String baseUrl=getBaseUrl(organizationId,null, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SystemLanguage>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SystemLanguage>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<List<SystemLanguage>>> response = restTemplate.exchange(
                baseUrl+"/system_language",
                HttpMethod.GET, null, resTypeReference);

        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
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
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }

}
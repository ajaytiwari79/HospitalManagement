package com.kairos.service.country.pay_level;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.user.pay_level.PayLevel;
import com.kairos.persistence.model.user.pay_level.PayLevelDTO;
import com.kairos.persistence.model.user.pay_level.PayLevelGlobalData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.user.pay_level.PaymentUnit.PER_HOUR;

/**
 * Created by prabjot on 26/12/17.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PayLevelIntegrationTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;

    static Long payLevelId;

    @Test
    @OrderTest(order = 1)
    public void getPayLevel(){

        String baseUrl= getBaseUrl(145L,53L,null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> response = restTemplate.exchange(
                baseUrl+"/pay_level",
                HttpMethod.GET, null, typeReference);
        RestTemplateResponseEnvelope<Map<String,Object>> responseBody = response.getBody();

        Assert.assertEquals(false,responseBody.getData().isEmpty());
    }

    @Test
    @OrderTest(order = 2)
    public void savePayLevel(){
        PayLevelDTO payLevelDTO = new PayLevelDTO("Test pay level",86L,6959L, PER_HOUR,new Date());
        String baseUrl= getBaseUrl(145L,53L,null);
        HttpEntity<PayLevelDTO> entity = new HttpEntity<>(payLevelDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PayLevel>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PayLevel>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PayLevel>> response = restTemplate.exchange(
                baseUrl+"/pay_level",
                HttpMethod.POST, entity, typeReference);
        RestTemplateResponseEnvelope<PayLevel> responseBody = response.getBody();
        payLevelId = responseBody.getData().getId();
        Assert.assertEquals(201,response.getStatusCodeValue());
        Assert.assertNotNull(payLevelId);
        Assert.assertEquals(responseBody.getData().getName(),payLevelDTO.getName());
    }




    public final String getBaseUrl(Long organizationId,Long countryId, Long unitId){
        if(organizationId!=null &&countryId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        }else if(organizationId!=null &&unitId!=null){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        }else if(organizationId!=null){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        }else{
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }
}

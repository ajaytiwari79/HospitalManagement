package com.kairos.client;

import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by prabjot on 17/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HouseHoldTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void addHouseHold(){
        String baseUrl=getBaseUrl(71L,5753L);
        ClientMinimumDTO clientMinimumDTO = new ClientMinimumDTO("Aage","Bag","2503681059");
        HttpEntity<ClientMinimumDTO> entity = new HttpEntity<>(clientMinimumDTO);
        ResponseEntity<ClientMinimumDTO> response = restTemplate.exchange(
                baseUrl+"/client/571/household",
                HttpMethod.POST, entity, ClientMinimumDTO.class);
       Assert.assertNotNull(response);
    }

    public   final String getBaseUrl(Long organizationId,Long unitId){
        if(organizationId!=null &&unitId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();                    ;
            return baseUrl;
        }else if(organizationId!=null){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        }else{
            throw new UnsupportedOperationException("ogranization ID must not be null");
        }

    }
}

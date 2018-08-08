package com.kairos.service.client;

import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.client.queryResults.ClientMinimumDTO;
import com.kairos.persistence.model.query_wrapper.ClientContactPersonStructuredData;
import com.kairos.user.staff.ContactPersonDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * Created by prabjot on 17/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ClientServiceIntegrationTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;


    /**
     * this test checks, client can't add himself in his house hold list
     */
    @Test
    public void validateCitizenAsHouseHold(){
        String baseUrl=getBaseUrl(71L,145L);
        ClientMinimumDTO clientMinimumDTO = new ClientMinimumDTO("Aage","Bag","2503681059", true);
        HttpEntity<ClientMinimumDTO> entity = new HttpEntity<>(clientMinimumDTO);
        ResponseEntity<ClientMinimumDTO> response = restTemplate.exchange(
                baseUrl+"/client/571/household",
                HttpMethod.POST, entity, ClientMinimumDTO.class);
       Assert.assertEquals(409,response.getStatusCodeValue());
    }

    @Test
    public void contactPersonShouldBeUpdate(){
        String baseUrl=getBaseUrl(71L,145L);
        ContactPersonDTO contactPersonDTO = new ContactPersonDTO(new Long(226),new Long(1005),
                Arrays.asList(new Long(192)));
        HttpEntity<ContactPersonDTO> entity = new HttpEntity<>(contactPersonDTO);
        ResponseEntity<ClientContactPersonStructuredData> response = restTemplate.exchange(
                baseUrl+"/client/571/staff/contact-person",
                HttpMethod.PUT, entity, ClientContactPersonStructuredData.class);
        System.out.println("response is " + response);
        Assert.assertEquals(200,response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void markClientAsDead(){
        String baseUrl=getBaseUrl(71L,145L);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/client/571/dead?deathDate=2017-11-11T07:03:44.155Z",
                HttpMethod.DELETE, null, String.class);
        System.out.println("response is " + response);
        Assert.assertEquals(200,response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }



    public final String getBaseUrl(Long organizationId,Long unitId){
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

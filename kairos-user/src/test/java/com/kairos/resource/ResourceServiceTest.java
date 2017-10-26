package com.kairos.resource;

import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.resources.FuelType;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.model.user.resources.ResourceDTO;
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

/**
 * Created by prabjot on 26/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ResourceServiceTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;

    /**
     * this test checks, client can't add himself in his house hold list
     */
    @Test
    public void addResource(){
        String baseUrl=getBaseUrl(145L,145L);
        ResourceDTO resourceDTO = new ResourceDTO("12345","12345","test",
                10F, FuelType.DIESEL,10600L);
        HttpEntity<ResourceDTO> entity = new HttpEntity<>(resourceDTO);
        ResponseEntity<Resource> response = restTemplate.exchange(
                baseUrl+"/resources",
                HttpMethod.POST, entity, Resource.class);
        Assert.assertNotNull(response.getBody().getId());
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

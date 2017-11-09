package com.kairos.service.country;

import com.google.gson.JsonObject;
import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.response.dto.web.OrganizationTypeDTO;
import com.kairos.response.dto.web.UpdateOrganizationTypeDTO;
import org.json.simple.JSONObject;
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
import java.util.Map;

/**
 * Created by prabjot on 8/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class OrganizationTypeIntegrationTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void createOrganizationTypeForCountry(){
        String baseUrl=getBaseUrl(71L,null);
        OrganizationTypeDTO organizationTypeDTO = new OrganizationTypeDTO("test123", Arrays.asList("country"));
        HttpEntity<OrganizationTypeDTO> entity = new HttpEntity<>(organizationTypeDTO);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/country/53/organization_type",
                HttpMethod.POST, entity, String.class);
        Assert.assertEquals(true,response.getBody().contains(organizationTypeDTO.getName()));
    }

    @Test
    public void updateOrganizationType(){
        String baseUrl=getBaseUrl(71L,null);
        Level level = new Level("Regional");
        level.setId(10980L  );
        UpdateOrganizationTypeDTO updateOrganizationTypeDTO = new UpdateOrganizationTypeDTO("test2",Arrays.asList(level),Arrays.asList());
        HttpEntity<UpdateOrganizationTypeDTO> entity = new HttpEntity<>(updateOrganizationTypeDTO);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/country/53/organization_type/10981",
                HttpMethod.PUT, entity, String.class);
        Assert.assertEquals(true,response.getBody().contains(updateOrganizationTypeDTO.getName()));
    }

    @Test
    public void getOrgTypesByCountryId(){
        String baseUrl=getBaseUrl(71L,null);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/country/53/organization_type",
                HttpMethod.GET, null, String.class);
        System.out.println(response.getBody());
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

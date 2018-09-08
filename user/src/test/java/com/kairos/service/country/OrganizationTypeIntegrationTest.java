package com.kairos.service.country;

import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.organization.Level;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.wrapper.UpdateOrganizationTypeDTO;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String baseUrl=getBaseUrl(4L,null);

        List<Long> levels=new ArrayList<>();
        levels.add(14L);
        OrganizationTypeDTO organizationTypeDTO = new OrganizationTypeDTO("Mytest", levels);
        HttpEntity<OrganizationTypeDTO> entity = new HttpEntity<>(organizationTypeDTO);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/country/4/organization_type",
                HttpMethod.POST, entity, String.class);
        Assert.assertEquals(true,response.getBody().contains(organizationTypeDTO.getName()));
    }

    @Test
    public void updateOrganizationType(){
        String baseUrl=getBaseUrl(71L,null);
        Level levelToDelete = new Level("Regional");
        levelToDelete.setId(10969L);
        Level levelToCreate = new Level("Junit");
        UpdateOrganizationTypeDTO updateOrganizationTypeDTO = new UpdateOrganizationTypeDTO("test3",Arrays.asList(),Arrays.asList(levelToDelete.getId()));
        HttpEntity<UpdateOrganizationTypeDTO> entity = new HttpEntity<>(updateOrganizationTypeDTO);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/country/53/organization_type/86",
                HttpMethod.PUT, entity, String.class);
        System.out.println("response " + response);
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

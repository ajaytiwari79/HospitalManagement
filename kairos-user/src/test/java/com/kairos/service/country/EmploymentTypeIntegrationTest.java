package com.kairos.service.country;

import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.dto.TimeTypeDTO;
import com.kairos.persistence.model.user.country.EmploymentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by prerna on 7/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EmploymentTypeIntegrationTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void addEmploymentType() throws Exception {
        String baseUrl=getBaseUrl(71L,53L);
        EmploymentType employmentType =new EmploymentType();
        employmentType.setName("Test Employment "+ new Date().toString());
        employmentType.setDescription("Test Employment Description");
        HttpEntity<EmploymentType> entity = new HttpEntity<>(employmentType);
        ResponseEntity<EmploymentType> response = restTemplate.exchange(
                baseUrl+"/employment_type",
                HttpMethod.POST, entity, EmploymentType.class);
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
    }

    @Test
    @Ignore
    public void updateEmploymentType() throws Exception {
        String baseUrl=getBaseUrl(71L,53L);
        String name="Test Employment Type "+ new Date().toString();
        EmploymentType employmentType =new EmploymentType();
        employmentType.setName(name);
        employmentType.setDescription(name+" Description");
        HttpEntity<EmploymentType> entity = new HttpEntity<>(employmentType);
        ResponseEntity<EmploymentType> response = restTemplate.exchange(
                baseUrl+"/employment_type/"+10656,
                HttpMethod.PUT, entity, EmploymentType.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
//        Assert.assertEquals(name,response.getBody().getName());
    }

    @Test
    @Ignore
    public void deleteEmploymentType() throws Exception {
        String baseUrl=getBaseUrl(71L,53L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/employment_type/"+10656);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.DELETE, null, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    @Ignore
    public void getEmploymentTypeList() throws Exception {
        String baseUrl=getBaseUrl(71L,53L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/employment_type/");
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    public final String getBaseUrl(Long organizationId,Long countryId){
        if(organizationId!=null &&countryId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        }else if(organizationId!=null){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        }else{
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }

}
package com.kairos.service.country;

import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.country.default_data.EmploymentTypeDTO;
import com.kairos.utils.DateUtil;
import org.junit.Assert;
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
        String baseUrl=getBaseUrl(71L,53L, null);
        EmploymentTypeDTO employmentTypeDTO =new EmploymentTypeDTO();
        employmentTypeDTO.setName("Test Employment "+ DateUtil.getCurrentDate().toString());
        employmentTypeDTO.setDescription("Test Employment Description");
        employmentTypeDTO.setAllowedForFlexPool(true);
        employmentTypeDTO.setAllowedForShiftPlan(true);
        employmentTypeDTO.setAllowedForContactPerson(true);
        HttpEntity<EmploymentTypeDTO> entity = new HttpEntity<>(employmentTypeDTO);
        ResponseEntity<EmploymentTypeDTO> response = restTemplate.exchange(
                baseUrl+"/employment_type",
                HttpMethod.POST, entity, EmploymentTypeDTO.class);
        System.out.println("response.getStatusCode() : "+response.getStatusCode());
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
    }

    @Test
    @Ignore
    public void updateEmploymentType() throws Exception {
        String baseUrl=getBaseUrl(71L,53L, null);
        String name="Test Employment Type "+ DateUtil.getCurrentDate().toString();


        EmploymentTypeDTO employmentTypeDTO =new EmploymentTypeDTO();
        employmentTypeDTO.setName(name);
        employmentTypeDTO.setDescription(name+" Description");
        employmentTypeDTO.setAllowedForFlexPool(true);
        employmentTypeDTO.setAllowedForShiftPlan(true);
        employmentTypeDTO.setAllowedForContactPerson(true);


        HttpEntity<EmploymentTypeDTO> entity = new HttpEntity<>(employmentTypeDTO);
        ResponseEntity<EmploymentTypeDTO> response = restTemplate.exchange(
                baseUrl+"/employment_type/"+10651,
                HttpMethod.PUT, entity, EmploymentTypeDTO.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
//        Assert.assertEquals(name,response.getBody().getName());
    }

    @Test
    @Ignore
    public void deleteEmploymentType() throws Exception {
        String baseUrl=getBaseUrl(71L,53L, null);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/employment_type/"+10651);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.DELETE, null, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    @Ignore
    public void getEmploymentTypeList() throws Exception {
        String baseUrl=getBaseUrl(71L,53L, null);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/employment_type/");
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void getOrganizationMappingDetailsTest() throws Exception {
        String baseUrl=getBaseUrl(71L,53L, null);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/employment_type_with_organizationType");
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    @Ignore
    public void addEmploymentTypeSettingsForOrganization() throws Exception {
        String baseUrl=getBaseUrl(71L,null, 145L);
        EmploymentTypeDTO employmentTypeDTO =new EmploymentTypeDTO();
        employmentTypeDTO.setAllowedForFlexPool(true);
        employmentTypeDTO.setAllowedForShiftPlan(true);
        employmentTypeDTO.setAllowedForContactPerson(true);

        HttpEntity<EmploymentTypeDTO> entity = new HttpEntity<>(employmentTypeDTO);
        ResponseEntity<EmploymentTypeDTO> response = restTemplate.exchange(
                baseUrl+"/employment_type/"+10651,
                HttpMethod.PUT, entity, EmploymentTypeDTO.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    @Ignore
    public void getEmploymentTypeSettingsForOrganization() throws Exception {
        String baseUrl=getBaseUrl(71L,null, 145L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/employment_type");
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
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
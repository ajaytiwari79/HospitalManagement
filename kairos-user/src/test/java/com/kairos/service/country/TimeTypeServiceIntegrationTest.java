package com.kairos.service.country;

import com.kairos.UserServiceApplication;
import com.kairos.persistence.model.dto.TimeTypeDTO;
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
 * Created by vipul on 1/11/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TimeTypeServiceIntegrationTest {
    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;



    @Test
    public void addTimeType() throws Exception {
        String baseUrl=getBaseUrl(71L,53L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/timeType/");

        String expectedUrl="http://localhost:8095/kairos/user/api/v1/organization/71/country/53/timeType";
        TimeTypeDTO timeTypeDTO =new TimeTypeDTO("pdsa1","PRESENCE DAY",false,false,false ,false);
        HttpEntity<TimeTypeDTO> entity = new HttpEntity<>(timeTypeDTO);
        ResponseEntity<TimeTypeDTO> response = restTemplate.exchange(
                baseUrl+"/timeType",
                HttpMethod.POST, entity, TimeTypeDTO.class);
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
      //  Assert.assertEquals(expectedUrl, builder.toUriString());
    }

    @Test
    public void getAllTimeTypes() throws Exception {
        String baseUrl=getBaseUrl(71L,53L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/timeType/");
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
    }
    // Please update the ID if required
    @Test

    public void deleteTimeType() throws Exception {
        String baseUrl=getBaseUrl(71L,53L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/timeType/"+7750);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.DELETE, null, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());

    }
    @Ignore
    @Test
    public void updateTimeType() throws Exception {

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
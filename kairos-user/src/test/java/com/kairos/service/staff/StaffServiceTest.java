package com.kairos.service.staff;

import com.kairos.UserServiceApplication;
import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.staff.StaffFilterDTO;
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

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * Created by oodles on 23/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StaffServiceTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;


    @Test
    public void addStaffFavouriteFilters() throws Exception {
        String baseUrl=getBaseUrl(71L,null);
        StaffFilterDTO staffFilterDTO = new StaffFilterDTO("tab_21"," {\"name\":\"el\",\"cprNumber\":\"\",\"phoneNumber\":\"\",\"taskTypes\":[],\"servicesTypes\":[],\"localAreaTags\":[],\"newDemands\":false,\"timeSlots\":[]}","my filter");
        HttpEntity<StaffFilterDTO> entity = new HttpEntity<>(staffFilterDTO);
        ResponseEntity<StaffFilterDTO> response = restTemplate.exchange(
                baseUrl+"/addStaffFavouriteFilters",
                HttpMethod.POST, entity, StaffFilterDTO.class);
        Assert.assertNotNull(response.getBody().getId());
    }


    public final String getBaseUrl(Long organizationId,Long unitId){
        if(organizationId!=null &&unitId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        }else if(organizationId!=null){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        }else{
            throw new UnsupportedOperationException("ogranization ID must not be null");
        }

    }

}
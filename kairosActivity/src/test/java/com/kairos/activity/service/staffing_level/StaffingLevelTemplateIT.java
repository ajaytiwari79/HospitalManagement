package com.kairos.activity.service.staffing_level;

import com.kairos.activity.KairosActivityApplication;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelDuration;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelSetting;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelTimeSlotDTO;
import com.kairos.activity.util.DateUtils;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalTime;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StaffingLevelTemplateIT {
    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;


    @Test
    @Ignore
    public void addStaffingLevelTemplate() {
        String baseUrl=getBaseUrl(1L,1L);

        HttpEntity<StaffingLevelDto> entity = new HttpEntity<StaffingLevelDto>(getStaffingLevelDTO());

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/staffing_level_template/",
                HttpMethod.POST, entity, String.class);
        String expected = "{ \"data\" : {\"phaseId\" : 1,}";
        Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }


    @Test
    public void getStaffingLevelTemplateByUnitIdAndDateTest() {
        String baseUrl=getBaseUrl(1L,1L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/staffing_level_template/")
                .queryParam("selectedDate","2017-11-10");

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());

    }

    private StaffingLevelDto getStaffingLevelDTO(){

        StaffingLevelDuration duration=new StaffingLevelDuration(LocalTime.now(),LocalTime.now());
        StaffingLevelSetting staffingLevelSetting=new StaffingLevelSetting(15,duration);
        StaffingLevelDto dto=new StaffingLevelDto(1L, DateUtils.getDate(),20L,staffingLevelSetting);
        List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlots=new ArrayList<>();
        StaffingLevelTimeSlotDTO timeSlotDTO1=new StaffingLevelTimeSlotDTO(0,5,10,new StaffingLevelDuration(LocalTime.now(),
                LocalTime.now()) );
        StaffingLevelTimeSlotDTO timeSlotDTO2=new StaffingLevelTimeSlotDTO(1,5,10,new StaffingLevelDuration(LocalTime.now(),
                LocalTime.now()) );
        StaffingLevelTimeSlotDTO timeSlotDTO3=new StaffingLevelTimeSlotDTO(2,5,10,new StaffingLevelDuration(LocalTime.now(),
                LocalTime.now()) );
        StaffingLevelTimeSlotDTO timeSlotDTO4=new StaffingLevelTimeSlotDTO(3,5,10,new StaffingLevelDuration(LocalTime.now(),
                LocalTime.now()) );
        staffingLevelTimeSlots.add(timeSlotDTO1);staffingLevelTimeSlots.add(timeSlotDTO2);
        staffingLevelTimeSlots.add(timeSlotDTO3); staffingLevelTimeSlots.add(timeSlotDTO4);
        dto.setPresenceStaffingLevelInterval(staffingLevelTimeSlots);
        return dto;
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

package com.kairos.activity.service.staffing_level;

import com.kairos.activity.KairosActivityApplication;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelDuration;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelSetting;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelTimeSlotDTO;
import com.kairos.activity.service.activity.ActivityService;
import com.kairos.activity.util.DateUtils;
import org.junit.Assert;
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

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StaffingLevelIntegrationTest {
    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;
    @Inject
    private StaffingLevelService staffingLevelService;
    @Inject
    private ActivityService activityService;


    @Test
    public void addStaffingLevel() {
        String baseUrl=getBaseUrl(95L,95L);

        HttpEntity<StaffingLevelDto> entity = new HttpEntity<StaffingLevelDto>(getStaffingLevelDTO());

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/staffing_level/",
                HttpMethod.POST, entity, String.class);
        Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());
    }


    @Test
    public void getStaffingLevelBetweenSpecifiedDatesTest() {
        String baseUrl=getBaseUrl(95L,95L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/staffing_level/")
                .queryParam("startDate","2017-11-10")
                .queryParam("endDate","2017-11-11");

        String expectedUrl="http://localhost:8090/kairos/activity/api/v1/organization/95/unit/95/staffing_level/?startDate=2017-11-10&endDate=2017-11-11";

        Assert.assertEquals(expectedUrl, builder.toUriString());

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

    @Test
    public void getStaffingLevelFromTimeCare(){
        staffingLevelService.getStaffingLevelFromTimeCare();
    }

    @Test
    public void getActivitesFromTimeCare(){
        activityService.getActivitesFromTimeCare();
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

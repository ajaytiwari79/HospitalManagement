package com.kairos.service.staffing_level;

import com.kairos.KairosActivityApplication;
import com.kairos.dto.activity.staffing_level.*;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.service.activity.ActivityService;
import com.kairos.commons.utils.DateUtils;
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
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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

        HttpEntity<PresenceStaffingLevelDto> entity = new HttpEntity<PresenceStaffingLevelDto>(getStaffingLevelDTO());

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


    private PresenceStaffingLevelDto getStaffingLevelDTO(){

        Duration duration=new Duration(LocalTime.now(),LocalTime.now());
        StaffingLevelSetting staffingLevelSetting=new StaffingLevelSetting(15,duration);
        PresenceStaffingLevelDto dto=new PresenceStaffingLevelDto(new BigInteger("1"), DateUtils.getDate(),20,staffingLevelSetting);
        List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlots=new ArrayList<>();
        StaffingLevelTimeSlotDTO timeSlotDTO1=new StaffingLevelTimeSlotDTO(0,5,10,new Duration(LocalTime.now(),
                LocalTime.now()) );
        StaffingLevelTimeSlotDTO timeSlotDTO2=new StaffingLevelTimeSlotDTO(1,5,10,new Duration(LocalTime.now(),
                LocalTime.now()) );
        StaffingLevelTimeSlotDTO timeSlotDTO3=new StaffingLevelTimeSlotDTO(2,5,10,new Duration(LocalTime.now(),
                LocalTime.now()) );
        StaffingLevelTimeSlotDTO timeSlotDTO4=new StaffingLevelTimeSlotDTO(3,5,10,new Duration(LocalTime.now(),
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

    @Test
    public void addStaffingLevelFromStaffingLevelTemplate(){
        String baseUrl=getBaseUrl(24L,2567L);

        HttpEntity<StaffingLevelFromTemplateDTO> entity = new HttpEntity<StaffingLevelFromTemplateDTO>(prePareDtoForStaffingLevelFromTemplate());

        ResponseEntity<Object> response = restTemplate.exchange(
                baseUrl+"/staffing_level/from_staffing_level_template/13",
                HttpMethod.POST, entity, Object.class);
        Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());
    }

   private StaffingLevelFromTemplateDTO prePareDtoForStaffingLevelFromTemplate(){
        List<DateWiseActivityDTO> dateWiseActivityDTOS =new ArrayList<>();
        Set<BigInteger> activities=new HashSet<>();
        Set<BigInteger> activitiess=new HashSet<>();
        activities.add(new BigInteger("2474"));
        activities.add(new BigInteger("2477"));
        activities.add(new BigInteger("2478"));
        activitiess.add(new BigInteger("2474"));
        activitiess.add(new BigInteger("2477"));
        LocalDate.of(2018,8,22);
        dateWiseActivityDTOS.add(new DateWiseActivityDTO(LocalDate.of(2018,8,22),activities));
        dateWiseActivityDTOS.add(new DateWiseActivityDTO(LocalDate.of(2018,8,24),activitiess));
        return new StaffingLevelFromTemplateDTO(new BigInteger("13"), dateWiseActivityDTOS);
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

package com.kairos.service.staffing_level;

import com.kairos.KairosActivityApplication;
import com.kairos.activity.staffing_level.Duration;
import com.kairos.activity.staffing_level.StaffingLevelSetting;
import com.kairos.activity.staffing_level.StaffingLevelTimeSlotDTO;
import com.kairos.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.staffing_level.StaffingLevelTemplate;
import com.kairos.util.DateUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StaffingLevelTemplateIntegrationTest {
    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;


    @Test
    @Ignore
    public void addStaffingLevelTemplate() {
        String baseUrl=getBaseUrl(1L,1L);

        HttpEntity<PresenceStaffingLevelDto> entity = new HttpEntity<>(getStaffingLevelDTO());

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/staffing_level_template/",
                HttpMethod.POST, entity, String.class);
        String expected = "{ \"data\" : {\"phaseId\" : 1,}";
        Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }


    @Test
    public void getStaffingLevelTemplateByUnitIdAndDateTest() {
        String baseUrl=getBaseUrl(24L,2567L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl+"/staffing_level_template/")
                .queryParam("selectedDate","2018-08-21");

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
    public void deleteStaffingLevelTemplate() {
        String baseUrl=getBaseUrl(24L,2567L);


        ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffingLevelTemplate>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffingLevelTemplate>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<StaffingLevelTemplate>> response = restTemplate.exchange(
                baseUrl+"/staffing_level_template/9",
                HttpMethod.DELETE, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));




//        ResponseEntity<String> response = restTemplate.exchange(
//                baseUrl+"/staffing_level_template/10",
//                HttpMethod.DELETE, null, String.class);
//        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
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

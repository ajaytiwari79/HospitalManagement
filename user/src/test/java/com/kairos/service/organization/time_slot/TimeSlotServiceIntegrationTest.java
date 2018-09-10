package com.kairos.service.organization.time_slot;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.kairos.enums.time_slot.TimeSlotMode.ADVANCE;
import static com.kairos.utils.DateUtil.ONLY_DATE;

/**
 * Created by prabjot on 6/12/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TimeSlotServiceIntegrationTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;
    private static final DateFormat df = new SimpleDateFormat(ONLY_DATE);

    @Test
    public void timeSlotShouldBeCreate(){

        TimeSlotDTO timeSlotDTO = new TimeSlotDTO("My test slot",9,0,18,0);
        Date startDate;
        Date endDate;
        try{
            startDate = df.parse("2018-07-01");
            endDate = df.parse("2018-09-30");
        } catch (ParseException e){
            throw new RuntimeException(e.getMessage());
        }
        TimeSlotSetDTO timeSlotSetDTO = new TimeSlotSetDTO("July-September", startDate,endDate, Arrays.asList(timeSlotDTO),ADVANCE);

        String baseUrl=getBaseUrl(145L,145L);
        HttpEntity<TimeSlotSetDTO> entity = new HttpEntity<>(timeSlotSetDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeSlotSet>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeSlotSet>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<TimeSlotSet>> response = restTemplate.exchange(
                baseUrl+"/time_slot",
                HttpMethod.POST, entity, typeReference);
        RestTemplateResponseEnvelope<TimeSlotSet> responseBody = response.getBody();
        Assert.assertEquals(200,response.getStatusCodeValue());
        Assert.assertEquals(responseBody.getData().getStartDate(),timeSlotSetDTO.getStartDate());

    }


    @Ignore
    @Test
    public void updateTimeSlotSet(){
        Date endDate;
        try {
            endDate = df.parse("2018-10-31");
        } catch (ParseException e){
            throw new RuntimeException(e.getMessage());
        }
        TimeSlotSetDTO timeSlotSetDTO = new TimeSlotSetDTO("Jan-May",endDate);

        String baseUrl=getBaseUrl(145L,145L);
        HttpEntity<TimeSlotSetDTO> entity = new HttpEntity<>(timeSlotSetDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlotSet>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlotSet>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<TimeSlotSet>>> response = restTemplate.exchange(
                baseUrl+"/time_slot_set/11124",
                HttpMethod.PUT, entity, typeReference);
        Assert.assertEquals(200,response.getStatusCodeValue());
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
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;

    }
}

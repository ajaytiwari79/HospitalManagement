//package com.kairos.scheduler.service.scheduler_panel;
//
//import com.kairos.commons.client.RestTemplateResponseEnvelope;
//import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
//import com.kairos.enums.scheduler.JobSubType;
//import com.kairos.enums.scheduler.JobType;
//import com.kairos.scheduler.config.OrderTest;
//import com.kairos.scheduler.config.app.SchedulerAppConfig;
//import org.apache.http.client.utils.URIBuilder;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.inject.Inject;
//import java.net.URISyntaxException;
//import java.time.DayOfWeek;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import java.util.List;
//import java.math.BigInteger;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SchedulerAppConfig.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//public class SchedulerPanelServiceIntegrationTest {
//    private final Logger logger = LoggerFactory.getLogger(SchedulerPanelServiceIntegrationTest.class);
//    @Value("${server.host.http.url}")
//    private String url;
//    @Inject
//    private TestRestTemplate restTemplate;
//    @Inject
//    private SchedulerPanelService schedulerPanelService;
//     private BigInteger createdId;
//     private String baseUrlWithUnit;
//
//    @Before
//    public void setUp() throws Exception {
//        baseUrlWithUnit = getBaseUrl(2567L);
//    }
////    @Test
////    @OrderTest(order = 1)
////    public void createSchedulerPanel() throws Exception {
////
////
////        List<DayOfWeek> days= Stream.of(DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,DayOfWeek.THURSDAY,DayOfWeek.FRIDAY,DayOfWeek.SATURDAY,DayOfWeek.SUNDAY).
////                collect(Collectors.toList());
////        List<String> selectedHours = Stream.of("00:00-00:59", "01:00-01:59", "02:00-02:59", "03:00-03:59", "04:00-04:59", "05:00-05:59", "06:00-06:59","07:00-07:59",
////                "08:00-08:59", "09:00-09:59", "10:00-10:59", "11:00-11:59", "12:00-12:59", "13:00-13:59", "14:00-14:59", "15:00-15:59", "16:00-16:59", "17:00-17:59",
////                "18:00-18:59", "19:00-19:59", "20:00-20:59", "21:00-21:59", "22:00-22:59", "23:00-23:59").collect(Collectors.toList());
////        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO("testJob",true,0,8,days,null,selectedHours,2567L,null,JobType.FUNCTIONAL,JobSubType.
////                QUESTIONAIRE_NIGHTWORKER,false,null,null);
////        HttpEntity<SchedulerPanelDTO> requestBodyData = new HttpEntity<>(schedulerPanelDTO);
////
////        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference =
////                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
////                };
////        ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> response = restTemplate.exchange(
////                baseUrlWithUnit + "/scheduler_panel",
////                HttpMethod.POST, requestBodyData, typeReference);
////        logger.info(response.toString());
////        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
////        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
////        Assert.assertTrue("0 0/8 00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23 ? * MON,TUE,WED,THU,FRI,SAT,SUN".
////                equals(response.getBody().getData().get("cronExpression")));
////
////        if(HttpStatus.OK.equals(response.getStatusCode())) {
////            createdId = new BigInteger(""+ response.getBody().getData().get("id"));
////            updateSchedulerPanel(createdId);
////            deleteSchedulerPanel(createdId);
////        }
////
////    }
//
//
//    public void updateSchedulerPanel(BigInteger createdId) throws Exception {
//
//
//        List<DayOfWeek> days= Stream.of(DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,DayOfWeek.THURSDAY,DayOfWeek.FRIDAY,DayOfWeek.SATURDAY,DayOfWeek.SUNDAY).
//                collect(Collectors.toList());
//        List<String> selectedHours = Stream.of("00:00-00:59", "01:00-01:59", "02:00-02:59", "03:00-03:59", "04:00-04:59", "05:00-05:59", "06:00-06:59","07:00-07:59",
//                "08:00-08:59", "09:00-09:59", "10:00-10:59", "11:00-11:59", "12:00-12:59", "13:00-13:59", "14:00-14:59", "15:00-15:59", "16:00-16:59", "17:00-17:59",
//                "18:00-18:59", "19:00-19:59", "20:00-20:59", "21:00-21:59", "22:00-22:59", "23:00-23:59").collect(Collectors.toList());
//        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO("testJobUpdated",true,0,8,days,null,selectedHours,2567L,null,JobType.FUNCTIONAL,JobSubType.
//                QUESTIONAIRE_NIGHTWORKER,false,null,null);
//        HttpEntity<SchedulerPanelDTO> requestBodyData = new HttpEntity<>(schedulerPanelDTO);
//
//        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference =
//                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
//                };
//        ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> response = restTemplate.exchange(
//                baseUrlWithUnit + "/scheduler_panel/"+createdId,
//                HttpMethod.PUT, requestBodyData, typeReference);
//        logger.info(response.toString());
//        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
//        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
//
//    }
//
//
//    public void getSchedulerPanelByUnitId() {
//
//        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map>>> typeReference =
//                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map>>>() {
//                };
//        ResponseEntity<RestTemplateResponseEnvelope<List<Map>>> response = restTemplate.exchange(
//                baseUrlWithUnit + "/scheduler_panel",
//                HttpMethod.GET, null, typeReference);
//        logger.info(response.toString());
//        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
//        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
//
//    }
//
//
//    public void deleteSchedulerPanel(BigInteger createdId) {
//
//        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference =
//                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
//                };
//        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
//                baseUrlWithUnit + "/scheduler_panel/"+createdId,
//                HttpMethod.DELETE, null, typeReference);
//        logger.info(response.toString());
//        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
//        logger.info("STATUS CODE ---------------------> {}",response.getStatusCode());
//
//    }
//
//
//
//    public final String getBaseUrl( Long unitId) {
//
//            String baseUrl = new StringBuilder(url + "/api/v1/unit/").append(unitId).toString();
//            return baseUrl;
//
//    }
//    public static <T> String getURI(T t, String uri, Map<String,Object> queryParams, Object... pathParams){
//        URIBuilder builder = new URIBuilder();
//
//        if(Optional.ofNullable(queryParams).isPresent()){
//            queryParams.entrySet().forEach(e->{
//                builder.addParameter(e.getKey(),e.getValue().toString());
//            });
//        }
//        try {
//            uri= uri+builder.build().toString();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return uri;
//    }
//
//
//
//}

package com.kairos.service.activity;

import com.kairos.KairosActivityApplication;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftService;
import com.kairos.wrapper.activity.ActivityTagDTO;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by vipul on 28/9/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShiftIntegrationServiceTest {

    private final Logger logger = LoggerFactory.getLogger(ShiftIntegrationServiceTest.class);

    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate testRestTemplate;
    @Mock
    private OrganizationRestClient organizationRestClient;

    @Mock
    private ActivityMongoRepository activityMongoRepository;
    @Mock
    private ActivityCategoryRepository activityCategoryRepository;

    @Mock
    private ShiftService shiftService;
    @Mock
    private PhaseService phaseService;
    @Mock
    MongoSequenceRepository mongoSequenceRepository;
    @Mock
    MongoTemplate mongoTemplate;
    @InjectMocks
    ActivityService activityService;
    static String baseUrlForCountry,baseUrlForUnit;
    static BigInteger createdId, createdIdDelete, wtaIdForUpdate;

    @Before
    public void setUp() throws Exception {
        baseUrlForCountry = getBaseUrl(71L, 53L, null);
        baseUrlForUnit = getBaseUrl(71L, null, 64L);


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createActivity() throws Exception {
        ActivityDTO activityDTO = new ActivityDTO("Meeting", "Description of meeting", 53L, "meeting_category", 95L, true);
        HttpEntity<ActivityDTO> requestBodyData = new HttpEntity<>(activityDTO);
        logger.info("baseUrlForCountry,{}", baseUrlForCountry + "/activity");
        ParameterizedTypeReference<RestTemplateResponseEnvelope<ActivityTagDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<ActivityTagDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<ActivityTagDTO>> response = testRestTemplate.exchange(
                baseUrlForCountry + "/activity", HttpMethod.POST, requestBodyData, typeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdId = response.getBody().getData().getId();
        logger.info("response", response + " New Id" + createdId);
    }

    @Test
    @Ignore
    public void findAllActivityTypeByCountry() throws Exception {

    }

    @Test
    @Ignore
    public void deleteActivityType() throws Exception {

        Activity at = new Activity();
        when(activityMongoRepository.findOne(Mockito.any(BigInteger.class))).thenReturn(at);
        activityService.deleteActivity(new BigInteger("12"));
        assertEquals(true, at.isDeleted());

    }

    @Test
    @Ignore
    public void updateGeneralTab() throws Exception {

    }

    @Test
    @Ignore
    public void getGeneralTabOfActivityType() throws Exception {

    }

    @Test
    @Ignore
    public void getBalanceSettingsTabOfActivityType() throws Exception {

    }

    @Test
    public void publishShifts() throws Exception {

        /*List<BigInteger> shifts = new ArrayList<>();
        shifts.add(new BigInteger("110"));
        shifts.add(new BigInteger("109"));
        ShiftPublishDTO shiftPublishDTO = new ShiftPublishDTO(shifts, ShiftStatus.FIXED);

        HttpEntity<ShiftPublishDTO> requestBodyData = new HttpEntity<>(shiftPublishDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, List<BigInteger>>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, List<BigInteger>>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Map<String, List<BigInteger>>>> response = testRestTemplate.exchange(
                baseUrlForUnit + "/publish_shifts", HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));*/
    }

    @Test
    public void deleteShiftAndOpenShiftsOnEmploymentEnd() {



        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> response = testRestTemplate.exchange(
                baseUrlForUnit + "/staff/8771/shifts_and_openshifts?employmentEndDate="+ DateUtils.getCurrentDayStartMillis(), HttpMethod.PUT, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }


    @Test
    public void deleteShifts() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrlForUnit + "/shift/" + 109).queryParam("type", "organization");
        ResponseEntity<String> response = testRestTemplate.exchange(
                builder.toUriString(),
                HttpMethod.DELETE, null, String.class);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    public final String getBaseUrl(Long organizationId, Long countryId, Long unitId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null && unitId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }
    }

}
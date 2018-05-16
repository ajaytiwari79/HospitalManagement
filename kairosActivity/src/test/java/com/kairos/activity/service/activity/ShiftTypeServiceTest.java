package com.kairos.activity.service.activity;

import com.kairos.activity.KairosActivityApplication;
import com.kairos.activity.client.OrganizationRestClient;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.common.MongoSequenceRepository;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.activity.ActivityTabsWrapper;
import com.kairos.activity.response.dto.activity.ActivityTagDTO;
import com.kairos.activity.service.phase.PhaseService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by vipul on 28/9/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShiftTypeServiceTest {

    private final Logger logger = LoggerFactory.getLogger(ShiftTypeServiceTest.class);

    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
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
    static String baseUrl;
    static BigInteger createdId, createdIdDelete, wtaIdForUpdate;

    @Before
    public void setUp() throws Exception {
        baseUrl = getBaseUrl(71L, 53L, null);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createActivity() throws Exception {
        ActivityDTO activityDTO = new ActivityDTO("Meeting", "Description of meeting", 53L, "meeting_category", 95L, true);
        HttpEntity<ActivityDTO> requestBodyData = new HttpEntity<>(activityDTO);
        logger.info("baseUrl,{}", baseUrl + "/activity");
        ParameterizedTypeReference<RestTemplateResponseEnvelope<ActivityTagDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<ActivityTagDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<ActivityTagDTO>> response = restTemplate.exchange(
                baseUrl + "/activity", HttpMethod.POST, requestBodyData, typeReference);

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
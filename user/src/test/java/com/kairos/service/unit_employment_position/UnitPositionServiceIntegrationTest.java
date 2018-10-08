package com.kairos.service.unit_employment_position;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.user.unit_position.UnitPositionSeniorityLevelQueryResult;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.staff.EmploymentService;
import com.kairos.service.unit_position.UnitPositionService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 27/2/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles(profiles = "test")

public class UnitPositionServiceIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(UnitPositionServiceIntegrationTest.class);
    @Value("${server.host.http.url}")
    private String url;
    @Inject
    TestRestTemplate restTemplate;
    @Inject
    EmploymentService employmentService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitPositionService unitPositionService;
    @Inject
    private UnitPositionGraphRepository unitPositionGraphRepository;
    static private Long createdId;
    static private String baseUrlWithUnit;
    @Inject
    private StaffGraphRepository staffGraphRepository;

    @Before
    public void setUp() throws Exception {
        baseUrlWithUnit = getBaseUrl(71L, null, 95L);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void updateSeniorityLevelOnJobTriggerTest() {

        Long expertiseStartDate = LocalDateTime.now().withYear(LocalDateTime.now().getYear()-3).withHour(0).withMinute(0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        staffGraphRepository.updateStaffExpertiseRelation(13316L,7799L,expertiseStartDate);
        unitPositionGraphRepository.deleteUnitPositionSeniorityLevel(13316L,7799L);
        unitPositionGraphRepository.createUnitPositionSeniorityLevelRelatioship(13316L,7799L,7796L);
        //unitPositionService.updateSeniorityLevelOnJobTrigger();
        UnitPositionSeniorityLevelQueryResult unitPositionSeniorityLevelQueryResult = unitPositionGraphRepository.getSeniorityLevelFromStaffUnitPosition(13316L,7799L);
        Assert.assertTrue(unitPositionSeniorityLevelQueryResult.getSeniorityLevel().getFrom()==3&&
                unitPositionSeniorityLevelQueryResult.getSeniorityLevel().getId()==7797);
    }
    @Test
    public void test2_getUnitPositionsOfStaff() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String, Object>>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String, Object>>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<Map<String, Object>>>> response = restTemplate.exchange(
                baseUrlWithUnit + "/unit_position/staff/" + 8051 + "?moduleId=tab_23&type=Organization",
                HttpMethod.GET, null, typeReference);
        logger.info(response.toString());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    public final String getBaseUrl(Long organizationId, Long countryId, Long unitId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null && unitId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            logger.info(baseUrl);
            return baseUrl;
        } else {
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }



}
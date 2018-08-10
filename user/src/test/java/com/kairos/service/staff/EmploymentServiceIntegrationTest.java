package com.kairos.service.staff;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.staff.employment.Employment;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vipul on 6/3/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmploymentServiceIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(EmploymentServiceIntegrationTest.class);
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    private TestRestTemplate restTemplate;
    static private Long createdId;
    static private String baseUrlWithUnit;
    static private Long staffId;
    @Inject
    private EmploymentService employmentService;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;

    @Before
    public void setUp() throws Exception {
        baseUrlWithUnit = getBaseUrl(71L, null, 95L);
        staffId = 5581L;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createUnitPermission() throws Exception {
        Map<String, Object> employmentDetail = new HashMap();
        employmentDetail.put("organizationId", 10711 + "");
        employmentDetail.put("roleId", 97 + "");
        employmentDetail.put("isCreated", true);
        employmentDetail.put("unit", 10711 + "");
        HttpEntity<Map<String, Object>> requestBodyData = new HttpEntity<>(employmentDetail);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> response = restTemplate.exchange(
                baseUrlWithUnit + "/staff/" + staffId + "/employment?moduleId=tab_23&type=Organization",
                HttpMethod.POST, requestBodyData, typeReference);
        logger.info(response.toString());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    }

    @Test
    public void moveToReadOnlyAccessGroup() {

        Optional<Employment> employment =  employmentGraphRepository.findById(8767L);
        employment.get().setAccessGroupIdOnEmploymentEnd(14628L);
        employmentGraphRepository.save(employment.get());
        Assert.assertTrue(employmentService.moveToReadOnlyAccessGroup(Stream.of(8767L).collect(Collectors.toList())));
        Long accessGroupId = accessGroupRepository.findAccessGroupByEmploymentId(8767L);
        Assert.assertTrue(accessGroupId.equals(14628L));

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
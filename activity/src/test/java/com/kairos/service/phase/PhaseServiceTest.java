package com.kairos.service.phase;

import com.kairos.KairosActivityApplication;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.commons.utils.DateUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by vipul on 29/9/17.
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PhaseServiceTest {
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    static BigInteger createdId, createdIdForDelete;

    @Mock
    private PhaseMongoRepository phaseMongoRepository;
    @Mock
    private OrganizationRestClient organizationRestClient;
    @Autowired
    private PhaseService phaseService;

    @Before
    public void setUp() throws Exception {
//        phaseService.createDefaultPhasesInCountry(53L);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    @Ignore
    public void getUnitPhaseByDate() throws Exception {
        LocalDate currentDate = LocalDate.now();
        PhaseDTO phaseDTO = new PhaseDTO();

        LocalDate proposedDate = DateUtils.getLocalDateFromDate(new Date("2017/11/15"));
        long weekDifference = currentDate.until(proposedDate, ChronoUnit.WEEKS);

        OrganizationDTO unitOrganization = new OrganizationDTO();
        List<PhaseDTO> phaseDTOList = new ArrayList<PhaseDTO>();
        when(organizationRestClient.getOrganization(Mockito.anyLong())).thenReturn(unitOrganization);
        when(phaseMongoRepository.getPhasesByUnit(Mockito.anyLong(), Sort.Direction.DESC)).thenReturn(phaseDTOList);
        phaseService.getUnitPhaseByDate(Mockito.anyLong(), new Date("2017/11/15"));
        assertEquals(false, false);
/*           when(activityTypeMongoRepository.findById(Mockito.any(BigInteger.class))).thenReturn(at);
        activityTypeService.deleteActivityType(new BigInteger("12"));
        assertEquals(false,at.getEnabled());*/
    }

    /*@Test
    public void createDefaultPhasesInCountry() throws Exception {
        phaseService.createDefaultPhasesInCountry(53L);
    }*/

    @Test
    public void createPhaseInCountry() throws Exception {
        PhaseDTO testPhase = new PhaseDTO("TEST", "TEST Phase", PhaseDefaultName.REQUEST, 1, DurationType.WEEKS, 19, 53L);
        String baseUrl = getBaseUrl(71L, null,53L);
        HttpEntity<PhaseDTO> entity = new HttpEntity<>(testPhase);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PhaseDTO>> response = restTemplate.exchange(
                baseUrl + "/phase",
                HttpMethod.POST, entity, typeReference);

        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
        createdIdForDelete = createdId = response.getBody().getData().getId();
    }

    @Test
    public void test2_getPhaseInCountry() throws Exception {
        String baseUrl = getBaseUrl(71L, null,53L);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PhaseDTO>> response = restTemplate.exchange(
                baseUrl + "/phase",
                HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }
    @Test
    public void test3_updatePhaseInCountry() throws Exception {
        PhaseDTO testPhase = new PhaseDTO("TEST", "TEST Phase", PhaseDefaultName.PUZZLE, 3, DurationType.WEEKS, 4, 53L);
        String baseUrl = getBaseUrl(71L, null,53L);
        HttpEntity<PhaseDTO> entity = new HttpEntity<>(testPhase);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PhaseDTO>> response = restTemplate.exchange(
                baseUrl + "/phase/"+createdId,
                HttpMethod.PUT, entity, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    }

    @Test
    public void test4_deletePhaseInCountry() throws Exception {
        String baseUrl = getBaseUrl(71L, null,53L);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PhaseDTO>> response = restTemplate.exchange(
                baseUrl + "/phase/"+createdIdForDelete,
                HttpMethod.DELETE, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }


    public final String getBaseUrl(Long organizationId, Long unitId, Long countryId) {
        if (organizationId != null && unitId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            ;
            return baseUrl;
        } else if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            ;
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("ogranization ID must not be null");
        }

    }

}
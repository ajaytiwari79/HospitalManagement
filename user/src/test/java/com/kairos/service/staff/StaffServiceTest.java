package com.kairos.service.staff;

import com.kairos.UserServiceApplication;
import com.kairos.enums.Gender;
import com.kairos.persistence.model.staff.StaffFilterDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Created by oodles on 23/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StaffServiceTest {

    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    private StaffService staffService;
    @Inject
    private ExceptionService exceptionService;
    @Inject private StaffRetrievalService staffRetrievalService;

    @Test
    public void addStaffFavouriteFilters() throws Exception {
        String baseUrl = getBaseUrl(71L, null);
        StaffFilterDTO staffFilterDTO = new StaffFilterDTO("tab_21", " {\"name\":\"el\",\"cprNumber\":\"\",\"phoneNumber\":\"\",\"taskTypes\":[],\"servicesTypes\":[],\"localAreaTags\":[],\"newDemands\":false,\"timeSlots\":[]}", "my filter");
        HttpEntity<StaffFilterDTO> entity = new HttpEntity<>(staffFilterDTO);
        ResponseEntity<StaffFilterDTO> response = restTemplate.exchange(
                baseUrl + "/addStaffFavouriteFilters",
                HttpMethod.POST, entity, StaffFilterDTO.class);
        Assert.assertNotNull(response.getBody().getId());
    }

    @Test
    public void createStaffFromWeb() throws Exception {
        String baseUrl = getBaseUrl(71L, 145L);
        StaffCreationDTO staffCreationDTO = new StaffCreationDTO("vipul", "pandey",
                Mockito.anyLong() + "", "", "pandeyVipul@a.com", Gender.MALE,
                "VIPUL", new Double(Math.random()).longValue(), 99L);
        HttpEntity<StaffCreationDTO> entity = new HttpEntity<>(staffCreationDTO);
        ResponseEntity<Staff> response = restTemplate.exchange(
                baseUrl + "/staff/create_staff_from_web",
                HttpMethod.POST, entity, Staff.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getStaffWithBasicInfo() throws Exception {
        String baseUrl = getBaseUrl(71L, 145L);
        ResponseEntity<Object> response = restTemplate.exchange(
                baseUrl + "staff?id=95&moduleId=tab_19&type=Organization",
                HttpMethod.GET, null, Object.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getAllStaffByUnitId() throws Exception {
        staffRetrievalService.getStaffWithBasicInfo(95L);
    }

    public final String getBaseUrl(Long organizationId, Long unitId) {
        if (organizationId != null && unitId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
    return null;
    }

}
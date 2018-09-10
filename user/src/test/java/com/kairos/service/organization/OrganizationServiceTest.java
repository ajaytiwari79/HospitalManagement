package com.kairos.service.organization;

import com.kairos.UserServiceApplication;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.dto.user.organization.ParentOrganizationDTO;
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

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created by prabjot on 9/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class OrganizationServiceTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;

    @Test
    public void createOrganization(){

        ParentOrganizationDTO parentOrganizationDTO = new ParentOrganizationDTO(Arrays.asList(94L),"Junit",
                true,"My pharmacy",null,Arrays.asList(86L),Arrays.asList(0L));
        parentOrganizationDTO.setVerifiedByGoogleMap(true);
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("Frederiksberg");
        addressDTO.setZipCodeId(65L);
        addressDTO.setZipCodeValue(2000);
        addressDTO.setMunicipalityId(59L);
        addressDTO.setHouseNumber("23");
        addressDTO.setFloorNumber(0);
        addressDTO.setCity("Frederiksberg");
        parentOrganizationDTO.setHomeAddress(addressDTO);

        String baseUrl=getBaseUrl(71L,null);
        HttpEntity<ParentOrganizationDTO> entity = new HttpEntity<>(parentOrganizationDTO);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/country/53/parent_organization",
                HttpMethod.POST, entity, String.class);
        Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());

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

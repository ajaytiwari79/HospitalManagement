package com.kairos.service.client_exception;

import com.kairos.KairosActivityApplication;
import com.kairos.persistence.model.client_exception.ClientExceptionDTO;
import org.junit.Assert;
import org.junit.Ignore;
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

import java.util.Arrays;

/**
 * Created by prabjot on 14/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ClientExceptionServiceIntegrationTest {

    @Value("${server.host.http.url}")
    private String url ;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void createException(){
        ClientExceptionDTO clientExceptionDTO = new ClientExceptionDTO();
        clientExceptionDTO.setSelectedDates(Arrays.asList("2017-12-16"));
        clientExceptionDTO.setExceptionTypeId("1");
        clientExceptionDTO.setFullDay(true);
        clientExceptionDTO.setTemporaryAddress(11081L);
        clientExceptionDTO.setHouseHoldMembers(Arrays.asList(7281L));
       /* AddressDTO addressDTO = new AddressDTO();
        addressDTO.setZipCodeValue(2000);
        addressDTO.setHouseNumber("23");
        addressDTO.setStreet("Smallegade");
        addressDTO.setMunicipalityId(59L);
        clientExceptionDTO.setTempAddress(addressDTO);*/

        String baseUrl=getBaseUrl(7247L,7247L);

        HttpEntity<ClientExceptionDTO> entity = new HttpEntity<>(clientExceptionDTO);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/client/10952/client_exception",
                HttpMethod.POST, entity, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Ignore
    @Test
    public void updateException(){
        ClientExceptionDTO clientExceptionDTO = new ClientExceptionDTO();
        clientExceptionDTO.setSelectedDates(Arrays.asList("2017-12-13"));
        clientExceptionDTO.setFromTime("2017-12-13T02:35:20.368Z");
        clientExceptionDTO.setToTime("2017-12-13T05:35:20.892Z");
        clientExceptionDTO.setExceptionTypeId("1");
        clientExceptionDTO.setFullDay(true);
        clientExceptionDTO.setTemporaryAddress(11081L);
        clientExceptionDTO.setHouseHoldMembers(Arrays.asList(7281L));
        clientExceptionDTO.setClientId(10952L);
       /* AddressDTO addressDTO = new AddressDTO();
        addressDTO.setZipCodeValue(2000);
        addressDTO.setHouseNumber("23");
        addressDTO.setStreet("Smallegade");
        addressDTO.setMunicipalityId(59L);
        clientExceptionDTO.setTempAddress(addressDTO);*/

        String baseUrl=getBaseUrl(7247L,7247L);

        HttpEntity<ClientExceptionDTO> entity = new HttpEntity<>(clientExceptionDTO);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/client_exception/243",
                HttpMethod.PUT, entity, String.class);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
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

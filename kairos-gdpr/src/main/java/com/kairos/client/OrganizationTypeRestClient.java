package com.kairos.client;


import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

@Service
public class OrganizationTypeRestClient {


    private static final Logger logger = LoggerFactory.getLogger(OrganizationTypeRestClient.class);

    @Autowired
    private RestTemplate restTemplate;


    public OrganizationTypeAndServiceResultDto getOrganizationTypeAndServices(OrganizationTypeAndServiceRestClientRequestDto requestDto) {

        final String baseUrl = RestClientURLUtils.getBaseUrl(true);
        try {

            HttpEntity<OrganizationTypeAndServiceRestClientRequestDto> entity=new HttpEntity<>(requestDto);
            ParameterizedTypeReference<RestTemplateResponseEnvelope< OrganizationTypeAndServiceResultDto>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope< OrganizationTypeAndServiceResultDto>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope< OrganizationTypeAndServiceResultDto>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/org_types_and_services/",
                            HttpMethod.POST,
                            entity, typeReference);
            RestTemplateResponseEnvelope< OrganizationTypeAndServiceResultDto> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());

            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());

        }

    }



    public List<OrganizationTypeAndServiceBasicDto> getOrganizationService(Set<Long> ids) {

         final String baseUrl = RestClientURLUtils.getBaseUrl(true);
        try {
            //String header="bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXRhaWxzIjp7ImlkIjoxOCwidXNlck5hbWUiOiJ1bHJpa0BrYWlyb3MuY29tIiwibmlja05hbWUiOiJVbHJpayIsImZpcnN0TmFtZSI6IlVscmlrIiwibGFzdE5hbWUiOiJSYXNtdXNzZW4iLCJlbWFpbCI6InVscmlrQGthaXJvcy5jb20iLCJwYXNzd29yZFVwZGF0ZWQiOnRydWUsImFnZSI6NjYsImNvdW50cnlJZCI6bnVsbCwiaHViTWVtYmVyIjpmYWxzZX0sImV4cCI6MTUyODYxNjA1MiwidXNlcl9uYW1lIjoidWxyaWtAa2Fpcm9zLmNvbSIsImp0aSI6ImQ0OGFlZDllLTNiMjktNDA2Zi05ZjMwLWY4MGM4MDU4Y2M4ZCIsImNsaWVudF9pZCI6ImthaXJvcyIsInNjb3BlIjpbIndlYmNsaWVudCJdfQ.sceT5Wv-g2-1Iqu0YkUDWVFKR492LHbBXW2vSrn0wq8";
            //HttpHeaders httpHeaders=new HttpHeaders();
            //httpHeaders.add("Authorization",header);
            HttpEntity<Set<Long>> entity=new HttpEntity<>(ids);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationTypeAndServiceBasicDto>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationTypeAndServiceBasicDto>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<OrganizationTypeAndServiceBasicDto>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/4/organization_services/",
                            HttpMethod.POST,
                            entity, typeReference);
            RestTemplateResponseEnvelope<List<OrganizationTypeAndServiceBasicDto>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());

            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }

    }




}

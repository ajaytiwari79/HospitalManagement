package com.kairos.client;


import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.response.dto.OrganizationTypeRestClientDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Service
public class OrganizationTypeRestClient {


    private static final Logger logger = LoggerFactory.getLogger(OrganizationTypeRestClient.class);


    @Inject
    private RestTemplate restTemplate;


    public List<OrganizationTypeRestClientDto> getOrganizationType(Set<Long> ids) {

        final String baseUrl = RestClientURLUtils.getBaseUrl(true);
        try {
            String header="bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXRhaWxzIjp7ImlkIjoxOCwidXNlck5hbWUiOiJ1bHJpa0BrYWlyb3MuY29tIiwibmlja05hbWUiOiJVbHJpayIsImZpcnN0TmFtZSI6IlVscmlrIiwibGFzdE5hbWUiOiJSYXNtdXNzZW4iLCJlbWFpbCI6InVscmlrQGthaXJvcy5jb20iLCJwYXNzd29yZFVwZGF0ZWQiOnRydWUsImFnZSI6NjYsImNvdW50cnlJZCI6bnVsbCwiaHViTWVtYmVyIjpmYWxzZX0sImV4cCI6MTUyODYxNjA1MiwidXNlcl9uYW1lIjoidWxyaWtAa2Fpcm9zLmNvbSIsImp0aSI6ImQ0OGFlZDllLTNiMjktNDA2Zi05ZjMwLWY4MGM4MDU4Y2M4ZCIsImNsaWVudF9pZCI6ImthaXJvcyIsInNjb3BlIjpbIndlYmNsaWVudCJdfQ.sceT5Wv-g2-1Iqu0YkUDWVFKR492LHbBXW2vSrn0wq8";
            HttpHeaders httpHeaders=new HttpHeaders();
            httpHeaders.add("Authorization",header);
            HttpEntity<Set<Long>> entity=new HttpEntity<>(ids,httpHeaders);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationTypeRestClientDto>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationTypeRestClientDto>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<OrganizationTypeRestClientDto>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/organization_type/",
                            HttpMethod.POST,
                            entity, typeReference);
            RestTemplateResponseEnvelope<List<OrganizationTypeRestClientDto>> response = restExchange.getBody();
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



    public List<OrganizationTypeRestClientDto> getOrganizationSubType(Set<Long> ids) {

        final String baseUrl = RestClientURLUtils.getBaseUrl(true);
        try {
            String header="bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXRhaWxzIjp7ImlkIjoxOCwidXNlck5hbWUiOiJ1bHJpa0BrYWlyb3MuY29tIiwibmlja05hbWUiOiJVbHJpayIsImZpcnN0TmFtZSI6IlVscmlrIiwibGFzdE5hbWUiOiJSYXNtdXNzZW4iLCJlbWFpbCI6InVscmlrQGthaXJvcy5jb20iLCJwYXNzd29yZFVwZGF0ZWQiOnRydWUsImFnZSI6NjYsImNvdW50cnlJZCI6bnVsbCwiaHViTWVtYmVyIjpmYWxzZX0sImV4cCI6MTUyODYxNjA1MiwidXNlcl9uYW1lIjoidWxyaWtAa2Fpcm9zLmNvbSIsImp0aSI6ImQ0OGFlZDllLTNiMjktNDA2Zi05ZjMwLWY4MGM4MDU4Y2M4ZCIsImNsaWVudF9pZCI6ImthaXJvcyIsInNjb3BlIjpbIndlYmNsaWVudCJdfQ.sceT5Wv-g2-1Iqu0YkUDWVFKR492LHbBXW2vSrn0wq8";
            HttpHeaders httpHeaders=new HttpHeaders();
            httpHeaders.add("Authorization",header);
            HttpEntity<Set<Long>> entity=new HttpEntity<>(ids,httpHeaders);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationTypeRestClientDto>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationTypeRestClientDto>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<OrganizationTypeRestClientDto>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/organization_sub_types/",
                            HttpMethod.POST,
                            entity, typeReference);
            RestTemplateResponseEnvelope<List<OrganizationTypeRestClientDto>> response = restExchange.getBody();
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

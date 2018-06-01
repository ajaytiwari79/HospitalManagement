package com.kairos.client.priority_group;

import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.user.staff.StaffBasicDetailsDTO;
import com.kairos.response.dto.web.UnitPositionWtaDTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.apache.http.client.utils.URIBuilder;
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

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static com.kairos.client.RestClientURLUtil.getBaseUrl;

@Service
public class PriorityGroupRestClient {
    private static Logger logger = LoggerFactory.getLogger(PriorityGroupRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    public <T, V> V publish(T t, Long id,boolean isUnit, IntegrationOperation integrationOperation,String uri, Map<String,Object> queryParams, Object... pathParams) {
        final String baseUrl = getBaseUrl(isUnit, id);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<V>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    restTemplate.exchange(
                            baseUrl +id + getURI(t,uri,queryParams,pathParams),
                            getHttpMethod(integrationOperation),
                            t==null?null:new HttpEntity<>(t), typeReference);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(response.getMessage());
            }
            return response.getData();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in activity micro service " + e.getMessage());
        }

    }

    public static HttpMethod getHttpMethod(IntegrationOperation integrationOperation) {
        switch (integrationOperation) {
            case CREATE:
                return HttpMethod.POST;
            case DELETE:
                return HttpMethod.DELETE;
            case UPDATE:
                return HttpMethod.PUT;
            case GET:
                return HttpMethod.GET;
            default:return null;

        }
    }
    public static <T> String getURI(T t,String uri,Map<String,Object> queryParams,Object... pathParams){
        URIBuilder builder = new URIBuilder();

        if(Optional.ofNullable(queryParams).isPresent()){
            queryParams.entrySet().forEach(e->{
                builder.addParameter(e.getKey(),e.getValue().toString());
            });
        }
        try {
            uri= uri+builder.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }
}
package com.kairos.scheduler.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.scheduler.config.EnvConfig;
import com.kairos.scheduler.service.exception.ExceptionService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.scheduler.rest_client.RestClientUrlUtil.getBaseUrl;

@Component
public class UserRestClientAuth {

    private static Logger logger = LoggerFactory.getLogger(UserRestClient.class);

    @Inject
    @Qualifier("restTemplateWithoutAuth")
    private RestTemplate restTemplate;


    @Inject
    private ExceptionService exceptionService;
    @Inject
    private EnvConfig env ;


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

    public <T extends Object, V> V publishRequest(T t, Long id, boolean isUnit, IntegrationOperation integrationOperation, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference,MultiValueMap<String,String> formParameters) {
        final String baseUrl = getBaseUrl(isUnit,id,env.getUserServiceUrlAuth());
        String url = baseUrl.replace("%2C+",",");
        try {

            HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
            restTemplate.getMessageConverters().add(formHttpMessageConverter);

            String authHeaderCode = env.getUserLoginApiAuthToken();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization",authHeaderCode);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<Map> httpEntity = new HttpEntity<>(formParameters,headers);

            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange = restTemplate.exchange(
                            url,
                            getHttpMethod(integrationOperation),
                            httpEntity, typeReference);

            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                logger.error("not valid code"+restExchange.getStatusCode());
                exceptionService.internalError(response.getMessage());
            }
            return response.getData();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in activity micro service " + e.getMessage());
        }

    }


    public String getURIWithParam(List<NameValuePair> queryParam){
        try {
            URIBuilder builder = new URIBuilder();
            if(queryParam!=null && !queryParam.isEmpty()) {
                builder.setParameters(queryParam);
            }
            return builder.build().toString();
        } catch (URISyntaxException e) {
            exceptionService.internalError(e.getMessage());
        }
        return null;
    }


}

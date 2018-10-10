package com.kairos.commons.utils;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.enums.IntegrationOperation;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.RestClientUrlUtil.getBaseUrl;
import org.apache.http.client.utils.URIBuilder;



@Component
public class UserRestClientAuth {

    private static Logger logger = LoggerFactory.getLogger(UserRestClientAuth.class);

    @Inject
    @Qualifier("restTemplateWithoutAuth")
    private RestTemplate restTemplate;
       @Inject
    private EnvConfigCommon env ;


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

/*
    public <T extends Object, V> V publishRequest(T t, Long id, boolean isUnit, IntegrationOperation integrationOperation, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference,MultiValueMap<String,String> formParameters) {
*/
public <V> Map<String,Object> publishRequest( Long id, boolean isUnit, IntegrationOperation integrationOperation,MultiValueMap<String,String> formParameters) {

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
            ParameterizedTypeReference<HashMap<String, Object>> responseType =
                    new ParameterizedTypeReference<HashMap<String, Object>>() {};

            ResponseEntity<HashMap<String,Object>> restExchange = restTemplate.exchange(
                            url,
                            getHttpMethod(integrationOperation),
                            httpEntity, responseType);
                       if (!restExchange.getStatusCode().is2xxSuccessful()) {
                logger.error("not valid code"+restExchange.getStatusCode());
                throw new InternalError((String)restExchange.getBody().get("message"));
            }
            return restExchange.getBody();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred while getting authtoken " + e.getMessage());
        }
        /*catch (JsonMappingException | JSONParseException e) {
            throw new RuntimeException("exception occurred while parsing " + e.getMessage());
        }
        catch(IOException e) {
            throw new RuntimeException("exception occurred while parsing " + e.getMessage());

        }*/
    }

    public String getURIWithParam(List<NameValuePair> queryParam){
        try {
            URIBuilder builder = new URIBuilder();
            if(CollectionUtils.isEmpty(queryParam)) {
                builder.setParameters(queryParam);
            }
            return builder.build().toString();
        } catch (URISyntaxException e) {
            throw new InternalError("error while creating URI");
            //commonsExceptionService.internalError(e.getMessage());
        }
    }


}

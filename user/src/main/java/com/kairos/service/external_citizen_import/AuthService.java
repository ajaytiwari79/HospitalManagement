package com.kairos.service.external_citizen_import;

import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by oodles on 18/4/17.
 */
@Transactional
@Service
public class AuthService {
    private long lastUpdated ;
    private static final long ONE_HOUR_MS = 3100000;
    public void kmdAuth()  {
        if( DateUtils.getCurrentDate().getTime()-lastUpdated < ONE_HOUR_MS){
            return;
        }
        dokmdAuth();
    }

    public void dokmdAuth()  {
        RestTemplate loginTemplate = new RestTemplate();
        HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        loginTemplate.getMessageConverters().add(formHttpMessageConverter);
        loginTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        loginTemplate.getMessageConverters().add(stringHttpMessageConverter);
        StringBuilder sb= new StringBuilder();
        sb.append(AppConstants.KMD_NEXUS_CLIENT_ID).append(":").append(AppConstants.KMD_NEXUS_CLIENT_SECRET);

        String authHeaderCode = Base64Utils.encodeToString(sb.toString().getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Basic "+authHeaderCode);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> bodyElements = new LinkedMultiValueMap<>();
        bodyElements.add("customer",AppConstants.KMD_NEXUS_CUSTOMER);
        bodyElements.add("grant_type",AppConstants.KMD_NEXUS_GRANT_TYPE);
        bodyElements.add("username",AppConstants.KMD_NEXUS_USERNAME);
        bodyElements.add("password",AppConstants.KMD_NEXUS_AUTH);
        HttpEntity<Map> headersElements = new HttpEntity<>(bodyElements,headers);

        String responseEntity = loginTemplate.postForObject(AppConstants.KMD_NEXUS_AUTH_URL,headersElements,String.class,bodyElements);
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        Map<String,Object> parsedData = jsonParser.parseMap(responseEntity);
        AppConstants.KMD_NEXUS_ACCESS_TOKEN = parsedData.get("access_token")+"";
        lastUpdated = DateUtils.getCurrentDate().getTime();
    }
}

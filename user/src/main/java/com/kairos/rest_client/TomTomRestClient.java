package com.kairos.rest_client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import io.netty.handler.codec.http.HttpConstants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author pradeep
 * @date - 12/6/18
 */
@Component
public class TomTomRestClient {

    @Inject
    private EnvConfig envConfig;

    public Map getfromTomtom(Map<String,String> requestParam) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        Map tomTomResponse = null;
        try {
            URIBuilder builder = getUrl(requestParam);
            HttpGet httppost = new HttpGet(builder.build());
            httppost.setHeader("Content-Type","application/json");
            HttpResponse response = httpclient.execute(httppost);
            ObjectMapper mapper = new ObjectMapper();
            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                tomTomResponse = mapper.readValue(response.getEntity().getContent(), Map.class);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }finally {
            ((CloseableHttpClient) httpclient).close();
        }
        return tomTomResponse;
    }

    private URIBuilder getUrl(Map<String,String> requestParam) throws URISyntaxException{
        URIBuilder builder = new URIBuilder(envConfig.getTomtomGeoCodeUrl());
        builder.setParameter(AppConstants.TOMTOM_KEY,envConfig.getTomtomKey());
        for (Map.Entry<String, String> entry : requestParam.entrySet()) {
            builder.setParameter(entry.getKey(),entry.getValue());
        }
        return builder;

    }


}

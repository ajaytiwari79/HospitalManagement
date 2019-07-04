package com.planner.service.taskPlanningService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


@Service
public class SolutionService {

    private static Logger logger = LoggerFactory.getLogger(SolutionService.class);


    public String sendSolutionToKairos(Object object,String url){
        return makeHttpCall(object,url);
    }

    public String makeHttpCall(Object object,String url) {
        JSONObject json;
        HttpClient client = HttpClientBuilder.create().build();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/Json");
        HttpUriRequest request=getPostRequest(new JSONObject(object), null, headers, url);
        StringBuffer result = new StringBuffer();
        try {
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            json = new JSONObject(result.toString());
        } catch(JSONException ex){
            System.out.println("Exception in json; "+result.toString());
            return null;
        }
        return result.toString();

        /*

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }*/
    }


        public HttpUriRequest getPostRequest(JSONObject body, Map<String, Object> urlParameters, Map<String, String> headers, String url){
            HttpPost postRequest = new HttpPost(url);
            if(headers == null) headers=new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            postRequest = (HttpPost) setHeaders(headers, postRequest);
            if(urlParameters != null){
                List<BasicNameValuePair> parametersList = new ArrayList<BasicNameValuePair>();
                for(Map.Entry<String, Object> entry : urlParameters.entrySet()){
                    parametersList.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
                }
                try {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parametersList);
                    postRequest.setEntity(entity);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if(body != null){
                HttpEntity entity = new ByteArrayEntity(body.toString().getBytes());
                postRequest.setEntity(entity);
            }
            return postRequest;
        }


    public HttpUriRequest setHeaders(Map<String, String> headers, HttpUriRequest request){
        if(headers != null){
            for(Entry<String, String> entry : headers.entrySet()){
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return request;
    }


}

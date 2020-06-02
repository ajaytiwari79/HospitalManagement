package com.kairos.shiftplanning.integration;

import com.amazonaws.HttpMethod;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;

public class RestClientUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(RestClientUtil.class);


    public static <T> ResponseDTO publishRequest(T entity, String url, com.amazonaws.HttpMethod httpMethod, Map<String,T> queryParam) throws IOException {

        url = ShiftPlanningSolver.serverAddress + url + getQueryParam(queryParam);
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestProperty("Content-Type","application/json");
        con.setRequestProperty("Authorization", UserContext.getAuthToken());
        con.setRequestMethod(httpMethod.name());
        if(httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT) && isNotNull(entity)){
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = ObjectMapperUtils.objectToJsonString(entity).getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }
        int responseCode = con.getResponseCode();
        LOGGER.debug("GET Response Code :: {}", responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                LOGGER.debug(response.toString());
                return ObjectMapperUtils.jsonStringToObject(response.toString(), ResponseDTO.class);
            }catch (Exception exception){
                LOGGER.error("Error {}",exception.getMessage());
            }
        } else {
            LOGGER.debug("request not worked");
        }
        return null;
    }

    private static <T> String getQueryParam(Map<String, T> queryParam) {
        StringBuilder stringBuilder = new StringBuilder();
        if(!queryParam.isEmpty()){
            stringBuilder.append("?");
            if(queryParam.entrySet().iterator().hasNext()){
                Map.Entry<String,T> entry = queryParam.entrySet().iterator().next();
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                if(queryParam.entrySet().iterator().hasNext()){
                    stringBuilder.append("&");
                }
            }
        }
        return stringBuilder.toString();
    }

}

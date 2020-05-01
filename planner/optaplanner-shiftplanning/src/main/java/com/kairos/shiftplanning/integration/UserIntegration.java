package com.kairos.shiftplanning.integration;

import com.amazonaws.HttpMethod;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;

public class UserIntegration {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserIntegration.class);

    public List<StaffDTO> getStaffData(List<Long> staffIds) {
        return publishRequest(staffIds,"/kairos/user/api/v1/unit/2403/get_all_staff_for_planning",HttpMethod.POST,new HashMap<>());
    }

    private <T> List<StaffDTO> publishRequest(T entity, String url, com.amazonaws.HttpMethod httpMethod, Map<String,T> queryParam) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(ShiftPlanningSolver.serverAddress+url+getQueryParam(queryParam)).openConnection();
            con.setRequestProperty("Content-Type","application/json");
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
                    return ObjectMapperUtils.jsonStringToList(response.toString(),StaffDTO.class);
                }catch (Exception exception){
                    LOGGER.error("Error {}",exception.getMessage());
                }
            } else {
                LOGGER.debug("request not worked");
            }
        }catch (IOException e){
            LOGGER.error("Error {}",e.getMessage());
        }

        return null;
    }

    private <T> String getQueryParam(Map<String, T> queryParam) {
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

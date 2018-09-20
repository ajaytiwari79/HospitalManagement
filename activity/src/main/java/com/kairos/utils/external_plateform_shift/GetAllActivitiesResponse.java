package com.kairos.utils.external_plateform_shift;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by oodles on 22/12/16.
 */
public class GetAllActivitiesResponse {

    @JacksonXmlProperty(localName = "Activity")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TimeCareActivity> GetAllActivitiesResult;

    public GetAllActivitiesResponse() {
    }

    public List<TimeCareActivity> getGetAllActivitiesResult() {
        return Optional.ofNullable(GetAllActivitiesResult).orElse(new ArrayList<>());
    }

    public void setGetAllActivitiesResult(List<TimeCareActivity> getAllActivitiesResult) {
        GetAllActivitiesResult = getAllActivitiesResult;
    }

    @Override
    public String toString() {
        return "GetAllActivitiesResponse{" +
                "GetAllActivitiesResult=" + GetAllActivitiesResult +
                '}';
    }
}

package com.kairos.util.external_plateform_shift;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Created by oodles on 22/12/16.
 */
public class GetAllActivitiesResponse {

    @JacksonXmlProperty
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Activity> GetAllActivitiesResult;

    public GetAllActivitiesResponse() {
    }

    public List<Activity> getGetAllActivitiesResult() {
        return GetAllActivitiesResult;
    }

    public void setGetAllActivitiesResult(List<Activity> getAllActivitiesResult) {
        GetAllActivitiesResult = getAllActivitiesResult;
    }
}

package com.kairos.util.external_plateform_shift;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Created by oodles on 20/12/16.
 */
public class GetEmploymentByIdResponse {
    @JacksonXmlProperty
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GetEmploymentByIdResult> GetEmploymentByIdResult;

    public GetEmploymentByIdResponse() {
    }

    public List<GetEmploymentByIdResult> getGetEmploymentByIdResult() {
        return GetEmploymentByIdResult;
    }

    public void setGetEmploymentByIdResult(List<GetEmploymentByIdResult> getEmploymentByIdResult) {
        GetEmploymentByIdResult = getEmploymentByIdResult;
    }
}

package com.kairos.utils.external_plateform_shift;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by oodles on 14/12/16.
 */
public class GetWorkShiftsFromWorkPlaceByIdResponse {

    @JacksonXmlProperty
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GetWorkShiftsFromWorkPlaceByIdResult> GetWorkShiftsFromWorkPlaceByIdResult;


    public GetWorkShiftsFromWorkPlaceByIdResponse() {
        //Default Constructor
    }

    //    public GetWorkShiftsFromWorkPlaceByIdResponse(com.kairos.user.domain.util.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResult[] getWorkShiftsFromWorkPlaceByIdResult) {
//        GetWorkShiftsFromWorkPlaceByIdResult = getWorkShiftsFromWorkPlaceByIdResult;
//    }

    public List<GetWorkShiftsFromWorkPlaceByIdResult> getGetWorkShiftsFromWorkPlaceByIdResult() {
        return GetWorkShiftsFromWorkPlaceByIdResult;
    }

    public void setGetWorkShiftsFromWorkPlaceByIdResult(List<GetWorkShiftsFromWorkPlaceByIdResult> getWorkShiftsFromWorkPlaceByIdResult) {
        GetWorkShiftsFromWorkPlaceByIdResult = getWorkShiftsFromWorkPlaceByIdResult;
    }


//    @Override
//    public String toString()
//    {
//        return "ClassPojo [GetWorkShiftsFromWorkPlaceByIdResult = "+GetWorkShiftsFromWorkPlaceByIdResult+"]";
//    }
}

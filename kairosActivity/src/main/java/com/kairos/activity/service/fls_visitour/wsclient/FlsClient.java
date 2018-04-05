package com.kairos.activity.service.fls_visitour.wsclient;

import de.tourenserver.*;

import java.util.Map;

/**
 * Created by Rama.Shankar on 27/9/16.
 * comment from https://timeadvice.fastleansmart.com/wsdl#ShowCallInfo
 * Use this webservice in a loop to transfer multiple calls.
 */
public interface FlsClient {
    /**
     * Method To create Call
     * @param payload
     * @return
     */
    CallResponse createCall(Call payload, Map<String, String> flsCredentials);

    /**
     * Get Call suggesstion
     * @param payload
     * @return
     */
    CallResponse getCallSuggestion(Call payload, Map<String, String> flsCredentials);

    /**
     * To update Call
     * required EXTID
     * @param payload
     * @return
     */
    CallResponse updateCall(Call payload, Map<String, String> flsCredentials);

    /**
     * To delete Call
     * required VTID
     * @param payload
     * @return
     */
    CallResponse deleteCall(Call payload, Map<String, String> flsCredentials);

    /**
     * to cancel call
     * @param payload
     * @return
     */
    CallResponse cancelCall(Call payload, Map<String, String> flsCredentials);


    /**
     * Method to confirm appointment
     * @param payload
     * @return
     */
    CallResponse confirmAppointment(Call payload, Map<String, String> flsCredentials);


    /**
     * To show call info
     * required VTID, EXTID
     * @param payload
     * @return
     */
    ShowCallInfoResponse showCallInfo(ShowCallInfo payload, Map<String, String> flsCredentials);

    /**
     * to validate address
     * @param geocode
     * @return
     */
    GeocodeResponse getGeoCode(Geocode geocode, Map<String, String> flsCredentials);

    /**
     * To create engineer(FieldManager)
     * @param fieldManager
     * @return fieldManagerResponse
     */
    FieldManagerResponse sendFieldManagerRequest(FieldManager fieldManager, Map<String, String> flsCredentials);

    /**
     *
     * @param fixSchedule
     * @return fixScheduleResponse
     */
    FixScheduleResponse sendFixScheduleRequest(FixSchedule fixSchedule, Map<String, String> flsCredentials);

    /**
     *To tranfer absences and working times for one engineer to VISITOUR
     * @param workSchedule
     * @return workScheduleResponse
     */
    WorkScheduleResponse sendWorkScheduleRequest(WorkSchedule workSchedule, Map<String, String> flsCredentials);

    /**
     * Optimization of a given period, region(s), teams(s), area(s) of expertise and engineer(s)
     * @param optimize
     * @return optimizeResponse
     */
    OptimizeResponse sendOptimizeScheduleRequest(Optimize optimize, Map<String, String> flsCredentials);
}

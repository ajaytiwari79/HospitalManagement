package com.kairos.activity.service.fls_visitour.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tourenserver.*;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Map;

/**
 * Created by oodles on 25/10/16.
 * Service to initialize Payload to be transmitted to FLS Visitour WebServices
 */
@Service
public class FlsPayloadService {

    /**
     * Initialize Call Object for creating Call in FLS Visitour.
     */
    public Call prepareCallPayload(Map callMetaData) {

        ObjectMapper objectMapper = new ObjectMapper();
        Call payload = objectMapper.convertValue(callMetaData, Call.class);

        return payload;
    }

    /**
     * Initialize GeoCode object for Address Verification.
     */
    public Geocode prepareGeoCodePayload(Map addressToVerify){

        ObjectMapper objectMapper = new ObjectMapper();
        Geocode geocode = objectMapper.convertValue(addressToVerify, Geocode.class);
        geocode.setFuzzy(true); //If true, a fuzzy search for the street is used. So that e.g. "Maiin Street" is automatically corrected to "Main Street".
        geocode.setCallSimulation(true); //If true, VISITOUR return the one and only address which would be taken if the address was sent through the CALL interface with an automatic geocoding.
        return geocode;

    }


    /**
     * Initialize ShowCallInfo object to get Call details.
     * @param callMetaData
     * @return showCallInfo
     */
    public ShowCallInfo prepareShowCallInfoPayload(Map callMetaData){

        ObjectMapper objectMapper = new ObjectMapper();
        ShowCallInfo showCallInfo = objectMapper.convertValue(callMetaData, ShowCallInfo.class);
        return showCallInfo;
    }

    /**
     * Initialize FieldManager object to create/update engineer in fls visitour.
     * @param engineerMetaData
     * @return fieldManager
     */
    public FieldManager prepareFieldManagerPayload(Map engineerMetaData){

        ObjectMapper objectMapper = new ObjectMapper();
        FieldManager fieldManager = objectMapper.convertValue(engineerMetaData, FieldManager.class);

        return fieldManager;
    }


    public FixSchedule prepareFixSchedulePayload(Map fixScheduleMetaData){

        ObjectMapper objectMapper = new ObjectMapper();
        FixSchedule fixSchedule = objectMapper.convertValue(fixScheduleMetaData, FixSchedule.class);

        return fixSchedule;
    }

    public WorkSchedule prepareWorkSchedulePayload(Map fixScheduleMetaData){

        ObjectMapper objectMapper = new ObjectMapper();
        WorkSchedule workSchedule = objectMapper.convertValue(fixScheduleMetaData, WorkSchedule.class);
        System.out.println(fixScheduleMetaData);
        return workSchedule;
    }

    public Optimize prepareOptimzePayload(Map optimzeScheduleMetaData){

        ObjectMapper objectMapper = new ObjectMapper();
        Optimize optimize= objectMapper.convertValue(optimzeScheduleMetaData, Optimize.class);

        return optimize;
    }
}

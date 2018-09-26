package com.kairos.service.fls_visitour.schedule;

import com.kairos.dto.activity.task.TaskAppointmentSuggestionDTO;
import com.kairos.rest_client.StaffRestClient;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.config.env.EnvConfig;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.fls_visitour.enums.FlsCallResponse;
import com.kairos.service.fls_visitour.wsclient.FlsClient;
import de.tourenserver.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringWriter;
import java.util.*;

import static com.kairos.constants.AppConstants.FORWARD_SLASH;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
@Service
public class SchedulerImpl implements  Scheduler{

    private static final Logger logger = LoggerFactory.getLogger(SchedulerImpl.class);
    /**
     * Web service client for fls
     */
    @Inject
    FlsClient client;

    @Inject
    private FlsPayloadService flsPayloadService;

   /* @Inject
    private StaffGraphRepository staffGraphRepository;
*/
    @Inject
    private EnvConfig envConfig;
    @Inject
    private ExceptionService exceptionService;

    @Autowired StaffRestClient staffRestClient;


    /*
    This method is required to convert Date to XMLGregorianCalendar.
    Need just date (excluding time) to send it to FLS Visitour.
     */
    private XMLGregorianCalendar getXMLGregorianCalendarDate(Date date){
        DateTime dateTime = new DateTime(date);
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());
            xmlDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            xmlDate.setHour(0);
            xmlDate.setMinute(0);
            xmlDate.setSecond(0);
            xmlDate.setMillisecond(0);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return xmlDate;
    }

    /*
    This method is required to generate XMLGregorianCalendar.
    Need just TIME (excluding Date) to send it to FLS Visitour.
     */
    private XMLGregorianCalendar getXMLGregorianCalendarTime(int hour , int minutes){

        XMLGregorianCalendar xmlTime = null;
        try {
            xmlTime = DatatypeFactory.newInstance().newXMLGregorianCalendarTime(hour,minutes,0, DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        //FLS Visitour just need the time frame here. (It ignores date)
        xmlTime.setYear(1);
        xmlTime.setMonth(1); //You cannot assign ZERO. So assigned 1. Moreover FLS INGNORES this in timeFrom and timeTo params.
        xmlTime.setDay(1);
        return xmlTime;
    }

    /*
This method is required to generate XMLGregorianCalendar.
Need just TIME (excluding Date) to send it to FLS Visitour.
 */
    private XMLGregorianCalendar getXMLGregorianCalendarTime(int hour , int minutes, int seconds){

        XMLGregorianCalendar xmlTime = null;
        try {
            xmlTime = DatatypeFactory.newInstance().newXMLGregorianCalendarTime(hour,minutes,seconds, DatatypeConstants.FIELD_UNDEFINED);
            //FLS Visitour just need the time frame here. (It ignores date)
            xmlTime.setYear(1);
            xmlTime.setMonth(1); //You cannot assign ZERO. So assigned 1. Moreover FLS INGNORES this in timeFrom and timeTo params.
            xmlTime.setDay(1);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return xmlTime;
    }

    @Override
    public int createCall(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials) {
        Call call  = flsPayloadService.prepareCallPayload(callMetaData);

        if(timeFrameInfo.get("dateFrom")!=null && timeFrameInfo.get("dateTo")!=null) {
            call.setDateFrom(getXMLGregorianCalendarDate((Date) timeFrameInfo.get("dateFrom")));
            call.setDateTo(getXMLGregorianCalendarDate((Date)timeFrameInfo.get("dateTo")));
        }

        if(timeFrameInfo.get("timeFrom")!=null && timeFrameInfo.get("timeTo")!=null){

            Date timeFrom = (Date) timeFrameInfo.get("timeFrom");
            Date timeTo = (Date) timeFrameInfo.get("timeTo");

            call.setTimeFrom(getXMLGregorianCalendarTime( timeFrom.getHours(), timeFrom.getMinutes()));
            call.setTimeTo(getXMLGregorianCalendarTime( timeTo.getHours(), timeTo.getMinutes()));

        }


        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Call.class);

            Marshaller marshaller = context.createMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.marshal(call, sw);
            String xmlString = sw.toString();
            logger.info("xmlString >>>>>>>>>>> "+xmlString);
        } catch (JAXBException e) {
            e.printStackTrace();
        }





        CallResponse response = client.createCall(call, flsCredentials);
        int code = response.getCallResult();
        int VTID=0;
        FlsCallResponse flsCallResponse = FlsCallResponse.getByCode(code);
        if(flsCallResponse.isError){
            logger.error("Error: " + flsCallResponse.message + " : " + response.getInfoText());
            exceptionService.schedulerException("message.exception.scheduler",flsCallResponse.message,response.getInfoText());
        }else{
            VTID = response.getVTID();
            logger.info(String.valueOf(VTID));
            logger.debug("call created do futhure" + flsCallResponse.message);

        }
        return VTID;
    }

    @Override
    public int updateCall(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials) {

        Call call  = flsPayloadService.prepareCallPayload(callMetaData);

        logger.debug("update call function code "+call.getFunctionCode());

        if(timeFrameInfo.get("dateFrom")!=null && timeFrameInfo.get("dateTo")!=null) {
            call.setDateFrom(getXMLGregorianCalendarDate((Date) timeFrameInfo.get("dateFrom")));
            call.setDateTo(getXMLGregorianCalendarDate((Date)timeFrameInfo.get("dateTo")));
        }

        if(timeFrameInfo.get("timeFrom")!=null && timeFrameInfo.get("timeTo")!=null){

            Date timeFrom = (Date) timeFrameInfo.get("timeFrom");
            Date timeTo = (Date) timeFrameInfo.get("timeTo");

            call.setTimeFrom(getXMLGregorianCalendarTime( timeFrom.getHours(), timeFrom.getMinutes()));
            call.setTimeTo(getXMLGregorianCalendarTime( timeTo.getHours(), timeTo.getMinutes()));

        }

        CallResponse response = client.updateCall(call, flsCredentials);
        int code = response.getCallResult();
        int VTID=0;
        FlsCallResponse flsCallResponse = FlsCallResponse.getByCode(code);
        if(flsCallResponse.isError){
            logger.error("Error: " + flsCallResponse.message + " : " + response.getInfoText());
            exceptionService.schedulerException("message.exception.scheduler",flsCallResponse.message,response.getInfoText());

        }else{
             VTID = response.getVTID();

        }
        return VTID;
    }

    @Override
    public int deleteCall(Map callMetaData, Map<String, String> flsCredentials) {
        Call call  = flsPayloadService.prepareCallPayload(callMetaData);
        CallResponse response = client.deleteCall(call, flsCredentials);
        int code = response.getCallResult();
        FlsCallResponse flsCallResponse = FlsCallResponse.getByCode(code);
        if(flsCallResponse.isError){
            logger.error("Error: " + flsCallResponse.message + " : " + response.getInfoText());
//            throw new SchedulerException(flsCallResponse.message + " : " + response.getInfoText());
            return 0;

        }else{
            int VTID = response.getVTID();
            return VTID;
        }

    }

    @Override
    public int cancelCallSchedule(Map callMetaData, Map<String, String> flsCredentials) {
        Call call  = flsPayloadService.prepareCallPayload(callMetaData);
        CallResponse response = client.cancelCall(call, flsCredentials);
        int code = response.getCallResult();
        int VTID=0;
        FlsCallResponse flsCallResponse = FlsCallResponse.getByCode(code);
        if(flsCallResponse.isError){
            logger.error("Error: " + flsCallResponse.message + " : " + response.getInfoText());
            exceptionService.schedulerException("message.exception.scheduler",flsCallResponse.message,response.getInfoText());
        }else{
             VTID = response.getVTID();

        }
        return VTID;
    }

    @Override
    public List<TaskAppointmentSuggestionDTO> getAppointmentSuggestions(Map callMetaData, Map<String, String> flsCredentials) {
        Call call  = flsPayloadService.prepareCallPayload(callMetaData);
        CallResponse response = client.getCallSuggestion(call, flsCredentials);
        int code = response.getCallResult();

        FlsCallResponse flsCallResponse = FlsCallResponse.getByCode(code);
        if(flsCallResponse.isError){
            logger.error("Error: " + flsCallResponse.message + " : " + response.getInfoText());
            //throw new InternalError(flsCallResponse.message + " : " + response.getInfoText());
exceptionService.runtimeException("message.exception.runtime",flsCallResponse.message,response.getInfoText());
        }

            int VTID = response.getVTID();
            ArrayOfAppointment arrayOfAppointment = response.getAppointments();
            List<Appointment> appointmentList = arrayOfAppointment.getAppointment();

        List<TaskAppointmentSuggestionDTO> taskAppointmentSuggestionDTOList = new ArrayList<>(appointmentList.size());
        TaskAppointmentSuggestionDTO taskAppointmentSuggestionDTO;
        for (Appointment appointment: appointmentList){
                taskAppointmentSuggestionDTO = new TaskAppointmentSuggestionDTO();

                taskAppointmentSuggestionDTO.setCost(appointment.getCost());
                taskAppointmentSuggestionDTO.setStaffId(Long.parseLong(appointment.getFMExtID()));

                 StaffDTO staffDTO = staffRestClient.getStaff(Long.parseLong(appointment.getFMExtID()));

               //Staff staff = staffGraphRepository.findById(Long.parseLong(appointment.getFMExtID()));
                taskAppointmentSuggestionDTO.setFirstName(staffDTO.getFirstName());
                taskAppointmentSuggestionDTO.setLastName(staffDTO.getLastName());
                taskAppointmentSuggestionDTO.setProfilePic(staffDTO.getProfilePic()!=null? envConfig.getServerHost() + FORWARD_SLASH + staffDTO.getProfilePic() : "");

                taskAppointmentSuggestionDTO.setSuggestedDate(appointment.getDate().toGregorianCalendar().getTime());
                taskAppointmentSuggestionDTO.setInfo(appointment.getInfo());
                taskAppointmentSuggestionDTOList.add(taskAppointmentSuggestionDTO);

            }

            return taskAppointmentSuggestionDTOList;


    }

    @Override
    public int confirmAppointment(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials) {

        Call call  = flsPayloadService.prepareCallPayload(callMetaData);

        logger.debug("confirmAppointment call function code "+call.getFunctionCode());

        if(timeFrameInfo.get("dateFrom")!=null && timeFrameInfo.get("dateTo")!=null) {
            call.setDateFrom(getXMLGregorianCalendarDate((Date) timeFrameInfo.get("dateFrom")));
            call.setDateTo(getXMLGregorianCalendarDate((Date)timeFrameInfo.get("dateTo")));
        }

        if(timeFrameInfo.get("timeFrom")!=null && timeFrameInfo.get("timeTo")!=null){

            Date timeFrom = (Date) timeFrameInfo.get("timeFrom");
            Date timeTo = (Date) timeFrameInfo.get("timeTo");

            call.setTimeFrom(getXMLGregorianCalendarTime( timeFrom.getHours(), timeFrom.getMinutes()));
            call.setTimeTo(getXMLGregorianCalendarTime( timeTo.getHours(), timeTo.getMinutes()));
        }

        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Call.class);

            Marshaller marshaller = context.createMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.marshal(call, sw);
            String xmlString = sw.toString();
            logger.info("confirmAppointment xmlString >>>>>>>>>>> "+xmlString);
        } catch (JAXBException e) {
            e.printStackTrace();
        }


        CallResponse response = client.confirmAppointment(call, flsCredentials);
        int code = response.getCallResult();
        logger.debug("code "+code);
        //TODO Skip exception while confirming call
        FlsCallResponse flsCallResponse = FlsCallResponse.getByCode(code);
        logger.error("flsCallResponse.message : >>>>>>  " + flsCallResponse.message + " : " + response.getInfoText());
       /* if(flsCallResponse.isError){
            logger.error("Error: " + flsCallResponse.message + " : " + response.getInfoText());
            throw new SchedulerException(flsCallResponse.message + " : " + response.getInfoText());
        }else{
            int VTID = response.getVTID();
            return VTID;
        }*/
        int VTID = response.getVTID();
        return VTID;

    }

    @Override
    public int confirmAppointmentSuggestion(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials) {

        Call call  = flsPayloadService.prepareCallPayload(callMetaData);

        logger.debug("confirmAppointment call function code "+call.getFunctionCode());

        if(timeFrameInfo.get("fixedDate")!=null ) {
            call.setFixedDate(getXMLGregorianCalendarDate((Date) timeFrameInfo.get("fixedDate")));
        }


        CallResponse response = client.confirmAppointment(call, flsCredentials);
        int code = response.getCallResult();
        logger.debug("code "+code);
        //TODO Skip exception while confirming call
        FlsCallResponse flsCallResponse = FlsCallResponse.getByCode(code);
        logger.error("flsCallResponse.message : >>>>>>  " + flsCallResponse.message + " : " + response.getInfoText());
       /* if(flsCallResponse.isError){
            logger.error("Error: " + flsCallResponse.message + " : " + response.getInfoText());
            throw new SchedulerException(flsCallResponse.message + " : " + response.getInfoText());
        }else{
            int VTID = response.getVTID();
            return VTID;
        }*/
        int VTID = response.getVTID();
        return VTID;

    }

    @Override
    public Map<String,Object> getGeoCode(Map addressToVerify, Map<String, String> flsCredentials) {

        Geocode geocode = flsPayloadService.prepareGeoCodePayload(addressToVerify);
        GeocodeResponse geocodeResponse = client.getGeoCode(geocode, flsCredentials);
        ArrayOfGeoCodeRec arrayOfGeoCodeRec = geocodeResponse.getGeocodeResult();
        List<GeoCodeRec> geoCodeRecList = arrayOfGeoCodeRec.getGeoCodeRec();
        GeoCodeRec geoCodeRec =  null;
        Map <String,Object> geoCodeResponse = new HashMap<>();

        if(geoCodeRecList.size() > 1){
            //FLS returned more than one address
        } else {
            geoCodeRec = geoCodeRecList.get(0);
            int geoCodeStatus= geoCodeRec.getGS();
            boolean isAddressVerified = false;
            String geoCodeMessage;

            if(geoCodeStatus == 9){
                isAddressVerified = true;
                geoCodeMessage = "Geocoding on house number level: this is the best geocoding, but not all road networks contain 100 percent house number information.";
            } else if(geoCodeStatus == 1 || geoCodeStatus == 2 || geoCodeStatus == 7){
                geoCodeMessage = "Geocoding on city centre level: street not present or not found.";
            } else if(geoCodeStatus == 3 || geoCodeStatus == 4 || geoCodeStatus == 8){
                geoCodeMessage = "Geocoding on street level: house number section was not found.";
            } else if(geoCodeStatus == 0){
                geoCodeMessage = "Not geocoded. The city was not found.";
            } else {
                geoCodeMessage = "Other geocoding";
            }
            logger.debug("Adding StatusCode: "+geoCodeStatus);
            geoCodeResponse.put("statusCode",geoCodeStatus);
            geoCodeResponse.put("isAddressVerified",isAddressVerified);
            geoCodeResponse.put("geoCodeMessage",geoCodeMessage);

            /*
            Comment By : Yasir
            According to documentation in https://timeadvice.fastleansmart.com/wsdl#Geocode
            It says 'In addition to the complete address, the result contains the GPS coordinates as integer values (format WGS84 * 100,000)'
            due to which coordinates are resulting in integer, so we have divided them by 100,000 to get decimals.
            We need coordinates in decimals to represent address in GoogleMaps

            MOST IMPORTANT : Below values are replaced.
             As GoogleMaps only placing Map pointer to correct location, when we enter Y-coordinate followed by X-coordinate.
             */
            geoCodeResponse.put("xCoordinates",( (double)geoCodeRec.getY()) /100000);// Read Above Comment
            geoCodeResponse.put("yCoordinates",( (double)geoCodeRec.getX()) /100000);// Read Above Comment
        }
        return geoCodeResponse;
    }

    @Override
    public CallInfoRec getCallInfo(Map callMetaData, Map<String, String> flsCredentials){

        ShowCallInfo showCallInfo = flsPayloadService.prepareShowCallInfoPayload(callMetaData);

        ShowCallInfoResponse showCallInfoResponse = client.showCallInfo(showCallInfo, flsCredentials);

        CallInfoRec callInfoRec = showCallInfoResponse.getShowCallInfoResult();

        logger.debug("callInfoRec "+callInfoRec.getVTID());
        logger.debug("callInfoRec "+callInfoRec.getCallInfo1());

        return callInfoRec;
    }

    @Override
    public int createEngineer(Map engineerMetaData, Map<String, String> flsCredentials){
        FieldManager fieldManager = flsPayloadService.prepareFieldManagerPayload(engineerMetaData);
        FieldManagerResponse fieldManagerResponse = client.sendFieldManagerRequest(fieldManager, flsCredentials);
        logger.debug("fieldManagerResponse.getFieldManagerResult() >> "+fieldManagerResponse.getFieldManagerResult());
        logger.debug("fieldManagerResponse.getFMVTID() >> "+fieldManagerResponse.getFMVTID());
        return fieldManagerResponse.getFieldManagerResult();
    }

    @Override
    public FixScheduleResponse getSchedule (Map fixScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials){
        FixSchedule fixSchedule = flsPayloadService.prepareFixSchedulePayload(fixScheduleMetaData);

        if(dateTimeInfo != null && dateTimeInfo.get("startDate")!=null && dateTimeInfo.get("endDate")!=null) {
            fixSchedule.setStart(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("startDate")));
            fixSchedule.setEnd(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("endDate")));
        }

        FixScheduleResponse fixScheduleResponse = client.sendFixScheduleRequest(fixSchedule, flsCredentials);
        /*ArrayOfFixedCall arrayOfFixedCall = fixScheduleResponse.getFixScheduleResult();
        List<FixedCall> fixedCallList = arrayOfFixedCall.getFixedCall();
        logger.info("fixedCallList size " +fixedCallList.size());
        for (FixedCall fixedCall : fixedCallList){
            logger.info("fixedCall VTID "+fixedCall.getVTID());
            logger.info("fixedCall ExtId "+fixedCall.getExtID());
            logger.info("fixedCall FMExtID "+fixedCall.getFMExtID());
            logger.info("fixedCall info "+fixedCall.getInfo());
            logger.info("fixedCall date "+fixedCall.getDate());
            logger.info("fixedCall state "+fixedCall.getState());
            logger.info("fixedCall Arrival "+fixedCall.getArrival());
            logger.info("fixedCall Distance "+fixedCall.getDistance());
            logger.info("fixedCall DrivingTime "+fixedCall.getDrivingTime());
            logger.info("fixedCall Sequence "+fixedCall.getSequence());
        }*/

        return fixScheduleResponse;
    }

    @Override
    public int assignWholeDayAbsence(Map workScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials){

        WorkSchedule workSchedule = flsPayloadService.prepareWorkSchedulePayload(workScheduleMetaData);

        if(dateTimeInfo.get("startDate")!=null && dateTimeInfo.get("endDate")!=null) {
            workSchedule.setStartDate(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("startDate")));
            workSchedule.setEndDate(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("endDate")));
        }
        if(dateTimeInfo.get("startTime")!=null && dateTimeInfo.get("endTime")!=null){
            workSchedule.setStartTime(getXMLGregorianCalendarTime( (int)dateTimeInfo.get("startTime"), 0));
            workSchedule.setEndTime(getXMLGregorianCalendarTime( (int)dateTimeInfo.get("endTime"), 0));
        }

        WorkScheduleResponse workScheduleResponse= client.sendWorkScheduleRequest(workSchedule, flsCredentials);
        return workScheduleResponse.getWorkScheduleResult();

    }


    @Override
    public int createEngineerWorkSchedule(Map workScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials){

//
//
//        if(dateTimeInfo.get("startDate")!=null && dateTimeInfo.get("endDate")!=null) {
//            workSchedule.setStartDate(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("startDate")));
//            workSchedule.setEndDate(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("endDate")));
//        }
//        if(dateTimeInfo.get("startTime")!=null && dateTimeInfo.get("endTime")!=null){
//            workSchedule.setStartTime(getXMLGregorianCalendarTime( (int)dateTimeInfo.get("startTime"), 0));
//            workSchedule.setEndTime(getXMLGregorianCalendarTime( (int)dateTimeInfo.get("endTime"), 0));
//        }
        workScheduleMetaData.put("startDate",getXMLGregorianCalendarDate((Date) dateTimeInfo.get("startDate")));
        workScheduleMetaData.put("endDate",getXMLGregorianCalendarDate((Date) dateTimeInfo.get("endDate")));
        //workScheduleMetaData.put("startTime",getXMLGregorianCalendarTime( (int)dateTimeInfo.get("startTime"), 0));
        //workScheduleMetaData.put("endTime",getXMLGregorianCalendarTime( (int)dateTimeInfo.get("endTime"), 0));
        workScheduleMetaData.put("days","1,2,3,4,5,6,7");
        WorkSchedule workSchedule = flsPayloadService.prepareWorkSchedulePayload(workScheduleMetaData);
//        logger.info("Work schedule meta data--->" + workSchedule);
        WorkScheduleResponse workScheduleResponse= client.sendWorkScheduleRequest(workSchedule, flsCredentials);
        return workScheduleResponse.getWorkScheduleResult();

    }

    @Override
    public int assignAbsencesToFLS(Map workScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials){

        WorkSchedule workSchedule = flsPayloadService.prepareWorkSchedulePayload(workScheduleMetaData);

        if(dateTimeInfo.get("startDate")!=null && dateTimeInfo.get("endDate")!=null) {
            workSchedule.setStartDate(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("startDate")));
            workSchedule.setEndDate(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("endDate")));
        }

        if(dateTimeInfo.get("startTimeSeconds") != null && dateTimeInfo.get("endTimeSeconds") != null){
            workSchedule.setStartTime(getXMLGregorianCalendarTime((int) dateTimeInfo.get("startTime"), (int) dateTimeInfo.get("startTimeMinute"), (int) dateTimeInfo.get("startTimeSeconds")));
            workSchedule.setEndTime(getXMLGregorianCalendarTime((int) dateTimeInfo.get("endTime"), (int) dateTimeInfo.get("endTimeMinute"), (int) dateTimeInfo.get("endTimeSeconds")));
        }else {
            if (dateTimeInfo.get("startTime") != null && dateTimeInfo.get("endTime") != null) {
                workSchedule.setStartTime(getXMLGregorianCalendarTime((int) dateTimeInfo.get("startTime"), (int) dateTimeInfo.get("startTimeMinute")));
                workSchedule.setEndTime(getXMLGregorianCalendarTime((int) dateTimeInfo.get("endTime"), (int) dateTimeInfo.get("endTimeMinute")));
            }
        }

        WorkScheduleResponse workScheduleResponse= client.sendWorkScheduleRequest(workSchedule, flsCredentials);
        return workScheduleResponse.getWorkScheduleResult();

    }



    @Override
    public int optmizeSchedule(Map optimizeScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials){

        Optimize optimize = flsPayloadService.prepareOptimzePayload(optimizeScheduleMetaData);

        if(dateTimeInfo.get("startDate")!=null && dateTimeInfo.get("endDate")!=null) {
            optimize.setStartDate(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("startDate")));
            optimize.setEndDate(getXMLGregorianCalendarDate((Date) dateTimeInfo.get("endDate")));
        }

        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Optimize.class);

            Marshaller marshaller = context.createMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.marshal(optimize, sw);
            String xmlString = sw.toString();
            logger.info("xmlString >>>>>>>>>>> "+xmlString);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        OptimizeResponse optimizeResponse = client.sendOptimizeScheduleRequest(optimize, flsCredentials);
        return optimizeResponse.getOptimizeResult();

    }


    @Override
    public int fixedAppointment(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials) {

        Call call  = flsPayloadService.prepareCallPayload(callMetaData);

        logger.debug("confirmAppointment call function code "+call.getFunctionCode());

        if(timeFrameInfo.get("dateFrom")!=null && timeFrameInfo.get("dateTo")!=null) {
            call.setDateFrom(getXMLGregorianCalendarDate((Date) timeFrameInfo.get("dateFrom")));
            call.setDateTo(getXMLGregorianCalendarDate((Date)timeFrameInfo.get("dateTo")));
        }

        if(timeFrameInfo.get("timeFrom")!=null && timeFrameInfo.get("timeTo")!=null){

            DateTime timeFrom = (DateTime) timeFrameInfo.get("timeFrom");
            DateTime timeTo = (DateTime) timeFrameInfo.get("timeTo");

            call.setTimeFrom(getXMLGregorianCalendarTime( timeFrom.getHourOfDay(), timeFrom.getMinuteOfHour()));
            call.setTimeTo(getXMLGregorianCalendarTime( timeTo.getHourOfDay(), timeTo.getMinuteOfHour()));
        }

        if(timeFrameInfo.get("fixedDate")!=null){
            DateTime fixedDate = (DateTime) timeFrameInfo.get("fixedDate");

            call.setFixedDate(getXMLGregorianCalendarDate( (Date) fixedDate.toDate()));
        }


        CallResponse response = client.confirmAppointment(call, flsCredentials);
        int code = response.getCallResult();
        logger.debug("code "+code);
        //TODO Skip exception while confirming call
        FlsCallResponse flsCallResponse = FlsCallResponse.getByCode(code);
        logger.error("flsCallResponse.message : >>>>>>  " + flsCallResponse.message + " : " + response.getInfoText());
       /* if(flsCallResponse.isError){
            logger.error("Error: " + flsCallResponse.message + " : " + response.getInfoText());
            throw new SchedulerException(flsCallResponse.message + " : " + response.getInfoText());
        }else{
            int VTID = response.getVTID();
            return VTID;
        }*/
        int VTID = response.getVTID();
        return VTID;

    }


}

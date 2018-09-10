package com.kairos.service.fls_visitour.wsclient;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.fls_visitour.webservice_config.WebServiceMessageSenderWithAuth;
import com.kairos.utils.FlsUrlPaths;
import com.kairos.utils.IPAddressUtil;
import de.tourenserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
@Service
public class FlsClientImpl implements  FlsClient{

    @Inject
    Jaxb2Marshaller marshaller;
    @Inject
    ExceptionService exceptionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger logger = LoggerFactory.getLogger(FlsClientImpl.class);

    /**
     * Method to send call request
     * request to https://timeadvice.fastleansmart.com/VTS/WS/call
     * @param payload
     * @return
     */
    private CallResponse sendCallRquest(Call payload, Map<String, String> flsCredentials){
        CallResponse response=null;
        try {
             response = (CallResponse) webServiceTemplate(flsCredentials)
                    .marshalSendAndReceive(flsCredentials.get("flsDefaultUrl") + "/" + FlsUrlPaths.CALL, payload);

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return response;
    }

    /**
     * Method to create call payload function 0
     * 0 = Create or update call
     * @param payload
     * @return
     */
    @Override
    public CallResponse createCall(Call payload, Map<String, String> flsCredentials) {

        try {
            if(0 != payload.getFunctionCode()){ /// function code must be zero
                exceptionService.flsCallException("message.fls.functioncode","0");
            }

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return sendCallRquest(payload, flsCredentials);
    }

    /**
     * Create and update has same fuction code
     * @param payload
     * @return
     */
    @Override
    public CallResponse updateCall(Call payload, Map<String, String> flsCredentials) {
        return createCall(payload, flsCredentials);
    }

    /**
     * Method to get List of call suggestions
     * 1 = Get appointment suggestions. Can be executed without a preceding FunctionCode 0
     * @param payload
     * @return
     */
    @Override
    public CallResponse getCallSuggestion(Call payload, Map<String, String> flsCredentials) {

        try {
            if(1 != payload.getFunctionCode()){ /// function code must be one
                exceptionService.flsCallException("message.fls.functioncode","1");
            }

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return sendCallRquest(payload, flsCredentials);
    }

    /**
     * 2 = Confirmation of an appointment: if FixedDate ist given then the call will be confirmed to this date. Otherwise VISITOUR will find the best day and confirm it. Can be executed without a preceding FunctionCode 0 or 1.
     * @param payload
     * @return
     */
    @Override
    public CallResponse confirmAppointment(Call payload, Map<String, String> flsCredentials) {

        try {
            if(2 != payload.getFunctionCode()){ /// function code must be 2
                exceptionService.flsCallException("message.fls.functioncode","2");
            }

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return sendCallRquest(payload, flsCredentials);
    }

    /**
     * 3 = Delete call. 4 = Delete if status less than "On route"
     * @param payload
     * @return
     */
    @Override
    public CallResponse deleteCall(Call payload, Map<String, String> flsCredentials) {

        try {
            if(3 != payload.getFunctionCode() &&  4 != payload.getFunctionCode() ){ /// function code must be 3, 4
                exceptionService.flsCallException("message.fls.functioncode","3 or 4");
            }

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return sendCallRquest(payload, flsCredentials);
    }

    /**
     * 5 = Cancel schedule of the call. Call will be unscheduled.
     * @param payload
     * @return
     */
    @Override
    public CallResponse cancelCall(Call payload, Map<String, String> flsCredentials) {

        try {
            if(5 != payload.getFunctionCode()){ /// function code must be 5
                exceptionService.flsCallException("message.fls.functioncode","5");
            }

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return sendCallRquest(payload, flsCredentials);
    }

    /**
     * Method to show call info
     * Request to https://timeadvice.fastleansmart.com/VTS/WS/ShowCallInfo
     * @param payload
     * @return
     */
    @Override
    public ShowCallInfoResponse showCallInfo(ShowCallInfo payload, Map<String, String> flsCredentials) {
        ShowCallInfoResponse showCallInfoResponse=null;
        try {
            showCallInfoResponse = (ShowCallInfoResponse) webServiceTemplate(flsCredentials)
                    .marshalSendAndReceive(flsCredentials.get("flsDefaultUrl") + "/" + FlsUrlPaths.SHOW_CALL_INFO, payload);

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return showCallInfoResponse;
    }

    /**
     * If address is valid then list of geocode address will recive
     * Request to https://timeadvice.fastleansmart.com/VTS/WS/Geocode
     * @param geocode
     * @return
     */
    @Override
    public GeocodeResponse getGeoCode(Geocode geocode, Map<String, String> flsCredentials) {
        GeocodeResponse response =null;
        try {
            response = (GeocodeResponse) webServiceTemplate(flsCredentials)
                    .marshalSendAndReceive(flsCredentials.get("flsDefaultUrl") + "/" + FlsUrlPaths.GEOCODE, geocode);

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return response;
    }

    /**
     * Request to https://timeadvice.fastleansmart.com/VTS/WS/FieldManager
     * @param fieldManager
     * @return fieldManagerResponse
     */
    @Override
    public FieldManagerResponse sendFieldManagerRequest(FieldManager fieldManager, Map<String, String> flsCredentials){
        FieldManagerResponse fieldManagerResponse=null;
        try {
            fieldManagerResponse =  (FieldManagerResponse) webServiceTemplate(flsCredentials)
                    .marshalSendAndReceive(flsCredentials.get("flsDefaultUrl") + "/" + FlsUrlPaths.FIELD_MANAGER, fieldManager);

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return fieldManagerResponse;
    }

    /**
     * Request to https://timeadvice.fastleansmart.com/VTS/WS/FixSchedule
     * @param fixSchedule
     * @return fixScheduleResponse
     */
    @Override
    public FixScheduleResponse sendFixScheduleRequest(FixSchedule fixSchedule, Map<String, String> flsCredentials){
        FixScheduleResponse fixScheduleResponse=null;
        try {
            fixScheduleResponse =  (FixScheduleResponse) webServiceTemplate(flsCredentials)
                    .marshalSendAndReceive(flsCredentials.get("flsDefaultUrl") + "/" + FlsUrlPaths.FIX_SCHEDULE, fixSchedule);

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");

        }
        return fixScheduleResponse;
    }

    /**
     * Request to https://timeadvice.fastleansmart.com/VTS/WS/WorkSchedule
     * @param workSchedule
     * @return workScheduleResponse
     */
    @Override
    public WorkScheduleResponse sendWorkScheduleRequest(WorkSchedule workSchedule, Map<String, String> flsCredentials){
        WorkScheduleResponse workScheduleResponse =null;
        try {
             workScheduleResponse =  (WorkScheduleResponse) webServiceTemplate(flsCredentials)
                    .marshalSendAndReceive(flsCredentials.get("flsDefaultUrl") + "/" + FlsUrlPaths.WORK_SCHEDULE, workSchedule);

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return workScheduleResponse;
    }

    /**
     * Request to https://timeadvice.fastleansmart.com/VTS/WS/Optimize
     * @param optimize
     * @return workScheduleResponse
     */
    @Override
    public OptimizeResponse sendOptimizeScheduleRequest(Optimize optimize, Map<String, String> flsCredentials){
        OptimizeResponse optimizeResponse =null;
        try {
            optimizeResponse =  (OptimizeResponse) webServiceTemplate(flsCredentials)
                    .marshalSendAndReceive(flsCredentials.get("flsDefaultUrl") + "/" + FlsUrlPaths.OPTIMIZE, optimize);

        } catch (Exception ex){
            exceptionService.flsCredentialException("message.fls.credentials");
        }
        return optimizeResponse;
    }

    public WebServiceTemplate webServiceTemplate(Map<String, String> auth) {

        if(auth == null || auth.get("flsDefaultUrl") == null || auth.get("flsDefaultUrl").trim().length() < 1){
            exceptionService.flsCredentialException("message.fls.credentials");
        }

        String loggedInUser = "";
        try{
            loggedInUser = (String)httpServletRequest.getAttribute("loggedInUser");
            loggedInUser = loggedInUser != null ? loggedInUser : "";
        }catch (Exception ex){
            logger.error("Exception "+ex);
        }

        String ipAddress = IPAddressUtil.getIPAddress(httpServletRequest);

        WebServiceTemplate client = new WebServiceTemplate();
        //ClientInterceptor [] logging = {new WebServiceLogging()};
        // client.setInterceptors(logging); // To log request and response
        client.setDefaultUri(auth.get("flsDefaultUrl"));
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        client.setMessageSender(new WebServiceMessageSenderWithAuth(auth, ipAddress, loggedInUser));
        return client;
    }


}

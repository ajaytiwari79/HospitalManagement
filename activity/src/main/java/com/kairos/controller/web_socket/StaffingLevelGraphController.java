package com.kairos.controller.web_socket;

import com.kairos.commons.utils.DateUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.ApiConstants;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.dto.activity.staffing_level.StaffingLevelTimeSlotDTO;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.messaging.wshandlers.StompClientWebSocketHandler;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.service.staffing_level.StaffingLevelService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;
import static com.kairos.constants.ApiConstants.API_V1;

@RestController
public class StaffingLevelGraphController {
    private static final Logger logger = LoggerFactory.getLogger(StaffingLevelGraphController.class);

    @Autowired
    private StaffingLevelService staffingLevelService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Inject
    private EnvConfig envConfig;

    @RequestMapping(value = API_UNIT_URL +"/staffing_level/graph", method = RequestMethod.GET)
    public PresenceStaffingLevelDto dynamicStaffingLevelGraphSyncResponsetest(){

        Duration duration=new Duration(LocalTime.now(),LocalTime.now());
        StaffingLevelSetting staffingLevelSetting=new StaffingLevelSetting(15,duration);
        PresenceStaffingLevelDto dto=new PresenceStaffingLevelDto(new BigInteger("1"), DateUtils.getDate(),20,staffingLevelSetting);
        List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlots=new ArrayList<>();

        for(int i=0;i<=95;i++){
            int Random = (int)(Math.random()*12);
            StaffingLevelTimeSlotDTO timeSlotDTO1=new StaffingLevelTimeSlotDTO(i,5,10,new Duration(LocalTime.of(0,0),
                    LocalTime.of(0,15)) );
            timeSlotDTO1.setAvailableNoOfStaff(Random);
            StaffingLevelActivity activity=new StaffingLevelActivity(new BigInteger("1"),6,6);
            timeSlotDTO1.getStaffingLevelActivities().add(activity);
            staffingLevelTimeSlots.add(timeSlotDTO1);
        }
        dto.setPresenceStaffingLevelInterval(staffingLevelTimeSlots);
        return dto;
    }


    @RequestMapping(value = API_V1 +"/staffing_level/test", method = RequestMethod.POST)
    public String testWebSocket() throws ExecutionException, InterruptedException {
        logger.info(" web socket responding");
        StaffingLevel staffingLevel = new StaffingLevel();
        staffingLevel.setCurrentDate(new Date());
        messagingTemplate.convertAndSend("/api/v1/ws/dynamic-push/staffing_level/graph/2403",staffingLevel);
        /*List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        SockJsClient sockJsClient = new SockJsClient(transports);
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("test","pradeep");
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(sockJsClient);
        ListenableFuture<StompSession> stompSessionListenableFuture = webSocketStompClient.connect(envConfig.getWsUrl(),new StompClientWebSocketHandler(2403l,jsonObject));
*/
        return "web socket responding";
    }
}

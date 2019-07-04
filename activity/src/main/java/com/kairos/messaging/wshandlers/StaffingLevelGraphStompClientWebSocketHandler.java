package com.kairos.messaging.wshandlers;

import com.kairos.constants.ApiConstants;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;

public class StaffingLevelGraphStompClientWebSocketHandler implements StompSessionHandler {

    Logger logger = LoggerFactory.getLogger(StaffingLevelGraphStompClientWebSocketHandler.class);

    private Long unitId;
    private StaffingLevel staffingLevel;

    public StaffingLevelGraphStompClientWebSocketHandler(Long unitId,StaffingLevel staffingLevel){
        this.unitId = unitId;
        this.staffingLevel = staffingLevel;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("is stomp connected ? "+session.isConnected());

        session.send(ApiConstants.API_V1+"/ws/dynamic-push/staffing-level/graph/"+this.unitId,staffingLevel);
        session.disconnect();
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.info("there was an exception"+exception.getMessage());
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        logger.info("transport error");
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return null;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        //This is override method
    }
}

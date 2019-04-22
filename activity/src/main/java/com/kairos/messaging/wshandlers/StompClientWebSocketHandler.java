package com.kairos.messaging.wshandlers;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;

/**
 * Created by neuron on 16/5/17.
 */
public class StompClientWebSocketHandler implements StompSessionHandler {

    Logger logger = LoggerFactory.getLogger(StompClientWebSocketHandler.class);

    private Long unitId;
    private JSONObject jsonObject;

    public StompClientWebSocketHandler(Long unitId,JSONObject clientsData){
        this.unitId = unitId;
        this.jsonObject = clientsData;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("is stomp connected ? "+session.isConnected());

        session.send("/api/v1/ws/planner/dynamic/"+this.unitId,jsonObject.toString().getBytes());
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
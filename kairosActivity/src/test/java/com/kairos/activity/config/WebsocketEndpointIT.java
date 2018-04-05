package com.kairos.activity.config;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
@ActiveProfiles("abc")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebsocketEndpointIT {
    private final Logger log = LoggerFactory.getLogger(WebsocketEndpointIT.class);
    @Value("${local.ws.server.port}")
    private int port;
    private String URL;

    private static final String SEND_GET_GRAPH_ENDPOINT = "/api/v1/ws/test/";
    private static final String SUBSCRIBE__GET_GRAPH_ENDPOINT = "/api/v1/ws/dynamic-push/test/";

    private CompletableFuture<String> completableFuture;
    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    @Before
    public void setup() {
        completableFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/kairos/activity/api/v1/ws";
    }



    @Before
    public void setUp() throws Exception {
        String wsUrl = "ws://127.0.0.1:" + port + "/ws";
            WebSocketClient webSocketClient = new StandardWebSocketClient();
            stompClient = new WebSocketStompClient(webSocketClient);
            stompClient.setMessageConverter(new StringMessageConverter());


           stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("web socket connected ");
            }

        }).get(1, SECONDS);


    }

    @After
    public void tearDown() throws Exception {
        stompSession.disconnect();
        stompClient.stop();
    }

    @Test
    public void connectsToSocket() throws Exception {

        Assert.assertTrue(stompSession.isConnected());
    }

    @Test
    public void receivesMessageFromSubscribedQueue() throws Exception {
        Long unitId=1L;
        stompSession.subscribe(SUBSCRIBE__GET_GRAPH_ENDPOINT + unitId, new CreateStaffingLevelGraphStompFrameHandler());
        //stompSession.send(SEND_GET_GRAPH_ENDPOINT + unitId, null);
        Thread.currentThread().sleep(1000);

        //when
        stompSession.send(SUBSCRIBE__GET_GRAPH_ENDPOINT + unitId,"test-payload");

        //then
        Assertions.assertThat(completableFuture.get(2, SECONDS)).isEqualTo("test-payload");
        //when

    }


     //used with sockJs connection
    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        return transports;
    }

    private class CreateStaffingLevelGraphStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            log.info("getting data payload {}",o);
            completableFuture.complete( o.toString());
        }
    }
}

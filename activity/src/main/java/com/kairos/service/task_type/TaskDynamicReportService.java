package com.kairos.service.task_type;

import com.kairos.config.env.EnvConfig;
import com.kairos.messaging.wshandlers.StompClientWebSocketHandler;
import com.kairos.persistence.model.task.Task;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neuron on 17/5/17.
 */

@Service
public class TaskDynamicReportService {

    Logger logger = LoggerFactory.getLogger(TaskDynamicReportService.class);

    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    private EnvConfig envConfig;


    public void aggregateReports(Task task){
        logger.info("creating reports and pushing to frontend");
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        SockJsClient sockJsClient = new SockJsClient(transports);
        JSONObject jsonObject = getClientsAggregatedData(task);
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(sockJsClient);
        webSocketStompClient.connect(envConfig.getWsUrl(),new StompClientWebSocketHandler(task.getUnitId(),jsonObject));

    }


    private  JSONObject getClientsAggregatedData(Task task){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("onEscalation",1);
        jsonObject.put("notDraggedAndDropped",0);
        jsonObject.put("updatedInfo",0);
        jsonObject.put("unplannedStatus",1);
        jsonObject.put("longDrivingTime",0);
        jsonObject.put("mostDriven",0);
        jsonObject.put("citizenId",task !=null ? task.getCitizenId():-1);
        return jsonObject;
    }

    public void sendCitizenDynamicReports(Long unitId, JSONObject citizenData){
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(sockJsClient);
        webSocketStompClient.connect(envConfig.getWsUrl(),new StompClientWebSocketHandler(unitId,citizenData));

    }


}

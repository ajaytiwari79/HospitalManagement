package com.kairos.activity.controller;
import com.kairos.activity.service.fls_visitour.dynamic_change.FLSVisitourChangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static com.kairos.activity.constants.ApiConstants.API_V1;

/**
 * Created by neuron on 5/5/17.
 */

@RestController
@RequestMapping(value = API_V1+"/messaging/manage/")
public class FLSVisitourChangeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    FLSVisitourChangeService flsVisitourChangeService;

    @RequestMapping(value = "/pushToQueue",method = RequestMethod.POST,produces = MediaType.APPLICATION_XML_VALUE)
    public String pushToQueue(@RequestBody String message) throws InterruptedException {
        logger.info("message received from dynamic change"+message);
        flsVisitourChangeService.pushToQueue("visitourChange",message);

        String messageVal = message.substring(message.indexOf("<ExtID"),message.indexOf("</ExtID>")+9);

        String xmlToReturn = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<soap:Body>\n" +
                "<DynamicChangeTestResponse xmlns=\"http://www.tourenserver.de/\">\n" +
                "<ExtID>"+messageVal+"</ExtID>\n" +
                "<InfoText>state change</InfoText>\n" +
                "<Result>0</Result>\n" +
                "</DynamicChangeTestResponse>\n" +
                "</soap:Body>\n" +
                "</soap:Envelope>";



        return xmlToReturn;
    }

    @RequestMapping(value = "/createQueue/{name}",method = RequestMethod.GET)
    public String registerNewQueue(@PathVariable String name){
        flsVisitourChangeService.registerReceiver(name);
        return "created";
    }





}

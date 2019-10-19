package com.kairos.messaging;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by neuron on 8/5/17.
 */


@JacksonXmlRootElement(localName = "DynamicChange")
@Getter
@Setter
public class ReceivedTask {

    @JacksonXmlProperty(localName = "VTID")
    private String vtid;

    @JacksonXmlProperty(localName = "ExtID")
    private String extid;

    @JacksonXmlProperty(localName = "FunctionCode")
    private String fucntionCode;

    @JacksonXmlProperty(localName = "Status")
    private int status;

    @JacksonXmlProperty(localName = "FMExtID")
    private String FMExtID;

    @JacksonXmlProperty(localName = "Termin_von")
    private Date earliestStartTime;

    @JacksonXmlProperty(localName = "Termin_bis")
    private Date latestStartTime;

    @JacksonXmlProperty(localName = "Plandauer")
    private int plannedDuration;

    @JacksonXmlProperty(localName = "Besuchsdauer")
    private int visitDuration;

    @JacksonXmlProperty(localName = "Planankunft")
    private String plannedArrival;

    @JacksonXmlProperty(localName = "Infotext")
    private String infoText;



    public String toString(){
        return new StringBuilder().append(this.getExtid()).append("_").append(this.getVtid()).toString();
    }




}

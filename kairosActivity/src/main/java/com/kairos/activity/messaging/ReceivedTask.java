package com.kairos.activity.messaging;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Date;

/**
 * Created by neuron on 8/5/17.
 */


@JacksonXmlRootElement(localName = "DynamicChange")
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


    public String getVtid() {
        return vtid;
    }

    public void setVtid(String vtid) {
        this.vtid = vtid;
    }

    public String getExtid() {
        return extid;
    }

    public void setExtid(String extid) {
        this.extid = extid;
    }

    public String getFucntionCode() {
        return fucntionCode;
    }

    public void setFucntionCode(String fucntionCode) {
        this.fucntionCode = fucntionCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFMExtID() {
        return FMExtID;
    }

    public void setFMExtID(String FMExtID) {
        this.FMExtID = FMExtID;
    }

    public Date getEarliestStartTime() {
        return earliestStartTime;
    }

    public void setEarliestStartTime(Date earliestStartTime) {
        this.earliestStartTime = earliestStartTime;
    }

    public Date getLatestStartTime() {
        return latestStartTime;
    }

    public void setLatestStartTime(Date latestStartTime) {
        this.latestStartTime = latestStartTime;
    }

    public int getPlannedDuration() {
        return plannedDuration;
    }

    public void setPlannedDuration(int plannedDuration) {
        this.plannedDuration = plannedDuration;
    }

    public int getVisitDuration() {
        return visitDuration;
    }

    public void setVisitDuration(int visitDuration) {
        this.visitDuration = visitDuration;
    }

    public String getPlannedArrival() {
        return plannedArrival;
    }

    public void setPlannedArrival(String plannedArrival) {
        this.plannedArrival = plannedArrival;
    }

    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }



    public String toString(){
        return new StringBuilder().append(this.getExtid()).append("_").append(this.getVtid()).toString();
    }




}

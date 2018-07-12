package com.kairos.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by oodles on 19/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportShiftDTO {
    private Date startTime;

    private String id;

    private EventResource eventResource;

    private String supplierId;

    private String status;

    private Date endTime;

    private String transportType;

    private String version;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public EventResource getEventResource ()
    {
        return eventResource;
    }

    public void setEventResource (EventResource eventResource)
    {
        this.eventResource = eventResource;
    }


    public String getSupplierId ()
    {
        return supplierId;
    }

    public void setSupplierId (String supplierId)
    {
        this.supplierId = supplierId;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }


    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [startTime = "+startTime+", id = "+id+", eventResource = "+eventResource+", supplierId = "+supplierId+", status = "+status+", endTime = "+endTime+", transportType = "+transportType+", version = "+version+"]";
    }
}

package com.kairos.activity.task;

import com.kairos.enums.Status;
import com.kairos.vrp.TaskAddress;

import java.util.Date;

public class TaskWrapper {
    String id;
    String name;
    int duration;
    TaskAddress address;
    Date dateFrom;
    Date dateTo;
    Status status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public TaskAddress getAddress() {
        return address;
    }

    public void setAddress(TaskAddress address) {
        this.address = address;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
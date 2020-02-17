package com.kairos.wrappers;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task.TaskAddress;
import com.kairos.persistence.model.task_demand.TaskDemand;

import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 6/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskWrapper {
    String id;
    String name;
    int duration;
    TaskAddress address;
    Date dateFrom;
    Date dateTo;
    Date timeFrom;
    Date timeTo;

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Date getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    TaskDemand.Status status;
    private List<Task.ClientException> clientExceptions;

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

    public TaskDemand.Status getStatus() {
        return status;
    }

    public void setStatus(TaskDemand.Status status) {
        this.status = status;
    }

    public List<Task.ClientException> getClientExceptions() {
        return clientExceptions;
    }

    public void setClientExceptions(List<Task.ClientException> clientExceptions) {
        this.clientExceptions = clientExceptions;
    }
}

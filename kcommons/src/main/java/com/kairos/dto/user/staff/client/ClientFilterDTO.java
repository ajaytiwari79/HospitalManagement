package com.kairos.dto.user.staff.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Jasgeet on 12/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientFilterDTO {
    private String name;
    private String cprNumber;
    private Long phoneNumber;
    private String clientStatus;
    private List<String> taskTypes;
    private List<Long> servicesTypes;
    private List<Long> localAreaTags;
    private boolean newDemands;
    private List<Long> timeSlots;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }

    public List<String> getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(List<String> taskTypes) {
        this.taskTypes = taskTypes;
    }

    public List<Long> getServicesTypes() {
        return servicesTypes;
    }

    public void setServicesTypes(List<Long> servicesTypes) {
        this.servicesTypes = servicesTypes;
    }

    public List<Long> getLocalAreaTags() {
        return localAreaTags;
    }

    public void setLocalAreaTags(List<Long> localAreaTags) {
        this.localAreaTags = localAreaTags;
    }

    public boolean isNewDemands() {
        return newDemands;
    }

    public void setNewDemands(boolean newDemands) {
        this.newDemands = newDemands;
    }

    public List<Long> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<Long> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }
}

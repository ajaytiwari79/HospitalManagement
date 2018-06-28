package com.kairos.activity.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by oodles on 25/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResources {

    private List<KMDTask> eventResources;

    public List<KMDTask> getEventResources() {
        return eventResources;
    }

    public void setEventResources(List<KMDTask> eventResources) {
        this.eventResources = eventResources;
    }
}

package com.kairos.activity.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.RequestType;

/**
 * Created by oodles on 22/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestComponent{
    private String description;
    private RequestType requestType;
    private String requestSentType;
    private String requestSentTo;
    private Long requestSentId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getRequestSentType() {
        return requestSentType;
    }

    public void setRequestSentType(String requestSentType) {
        this.requestSentType = requestSentType;
    }

    public Long getRequestSentId() {
        return requestSentId;
    }

    public void setRequestSentId(Long requestSentId) {
        this.requestSentId = requestSentId;
    }

    public String getRequestSentTo() {
        return requestSentTo;
    }

    public void setRequestSentTo(String requestSentTo) {
        this.requestSentTo = requestSentTo;
    }
}

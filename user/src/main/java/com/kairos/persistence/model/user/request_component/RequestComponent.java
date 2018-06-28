package com.kairos.persistence.model.user.request_component;

import com.kairos.enums.RequestType;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 22/8/17.
 */
@NodeEntity
public class RequestComponent extends UserBaseEntity {
    private String description;
    private RequestType requestType;
    private String requestSentType;
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
}

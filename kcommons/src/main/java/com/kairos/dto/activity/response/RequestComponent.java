package com.kairos.dto.activity.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.RequestType;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 22/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RequestComponent{
    private String description;
    private RequestType requestType;
    private String requestSentType;
    private String requestSentTo;
    private Long requestSentId;
}

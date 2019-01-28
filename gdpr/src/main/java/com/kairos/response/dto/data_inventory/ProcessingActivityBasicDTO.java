package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityBasicDTO {

    private BigInteger processingActivityId;
    private String processingActivityName;
    private boolean isSubProcessingActivity;

    public BigInteger getProcessingActivityId() {
        return processingActivityId;
    }

    public void setProcessingActivityId(BigInteger processingActivityId) {
        this.processingActivityId = processingActivityId;
    }

    public String getProcessingActivityName() {
        return processingActivityName;
    }

    public void setProcessingActivityName(String processingActivityName) {
        this.processingActivityName = processingActivityName;
    }

    public boolean isSubProcessingActivity() {
        return isSubProcessingActivity;
    }

    public void setSubProcessingActivity(boolean subProcessingActivity) {
        isSubProcessingActivity = subProcessingActivity;
    }

    public ProcessingActivityBasicDTO() {
    }

    public ProcessingActivityBasicDTO(BigInteger processingActivityId, String processingActivityName, boolean subProcessingActivity) {
        this.processingActivityId = processingActivityId;
        this.processingActivityName = processingActivityName;
        this.isSubProcessingActivity = subProcessingActivity;
    }
}

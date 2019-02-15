package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityBasicDTO {

    private Long processingActivityId;
    private String processingActivityName;
    private boolean isSubProcessingActivity;

    public Long getProcessingActivityId() {
        return processingActivityId;
    }

    public void setProcessingActivityId(Long processingActivityId) {
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

    public ProcessingActivityBasicDTO(Long processingActivityId, String processingActivityName, boolean isSubProcessingActivity) {
        this.processingActivityId = processingActivityId;
        this.processingActivityName = processingActivityName;
        this.isSubProcessingActivity = isSubProcessingActivity;
    }
}

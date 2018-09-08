package com.kairos.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

/**
 * Created by vipul on 24/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotesActivityDTO {
    private BigInteger activityId;
    private String content;
    private String originalDocumentName;
    private String modifiedDocumentName;

    public NotesActivityDTO() {
        //Default Constructor
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOriginalDocumentName() {
        return originalDocumentName;
    }

    public void setOriginalDocumentName(String originalDocumentName) {
        this.originalDocumentName = originalDocumentName;
    }

    public String getModifiedDocumentName() {
        return modifiedDocumentName;
    }

    public void setModifiedDocumentName(String modifiedDocumentName) {
        this.modifiedDocumentName = modifiedDocumentName;
    }
}

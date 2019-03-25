package com.kairos.response.dto.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedProcessingActivityResponseDTO {

    private Long id;

    private String name;

    private boolean subProcessingActivity;

    private RelatedProcessingActivityResponseDTO parent;

    public RelatedProcessingActivityResponseDTO() {
    }

    public RelatedProcessingActivityResponseDTO(Long id, String name, boolean subProcessingActivity, BigInteger parentProcessingActivityId, String parentProcessingActivityName) {
        this.id = id;
        this.name = name;
        this.subProcessingActivity = subProcessingActivity;
        if (parentProcessingActivityId != null) {
            this.parent = new RelatedProcessingActivityResponseDTO(parentProcessingActivityId.longValue(), parentProcessingActivityName);
        }
    }

    public RelatedProcessingActivityResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSubProcessingActivity() {
        return subProcessingActivity;
    }

    public void setSubProcessingActivity(boolean subProcessingActivity) {
        this.subProcessingActivity = subProcessingActivity;
    }

    public RelatedProcessingActivityResponseDTO getParent() {
        return parent;
    }

    public void setParent(RelatedProcessingActivityResponseDTO parent) {
        this.parent = parent;
    }
}

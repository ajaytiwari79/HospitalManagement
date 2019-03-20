package com.kairos.response.dto.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedProcessingActivityResponseDTO {

    private Long id;

    private String name;

    private boolean subProcessingActivity;

    private RelatedProcessingActivityResponseDTO parent;

    public RelatedProcessingActivityResponseDTO() {
    }

    public RelatedProcessingActivityResponseDTO(Long id, String name ,boolean subProcessingActivity) {
        this.id = id;
        this.name = name;
        this.subProcessingActivity=subProcessingActivity;
    }



    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public boolean isSubProcessingActivity() { return subProcessingActivity; }

    public void setSubProcessingActivity(boolean subProcessingActivity) { this.subProcessingActivity = subProcessingActivity; }

    public RelatedProcessingActivityResponseDTO getParent() { return parent; }

    public void setParent(RelatedProcessingActivityResponseDTO parent) { this.parent = parent; }
}

package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseBasicDTO {


    private BigInteger id;

    private String title;

    private String description;

    private Boolean requireUpdate=false;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequireUpdate() {
        return requireUpdate;
    }

    public void setRequireUpdate(Boolean requireUpdate) {
        this.requireUpdate = requireUpdate;
    }

    public ClauseBasicDTO() {
    }
}

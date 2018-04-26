package com.kairos.persistence.model.timetype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 17/10/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeTypeDTO  {
    private BigInteger id;
    private String timeTypes;
    private String label;
    private String description;
    private BigInteger UpperLevelTimeTypeId;
    private boolean selected;
    private List<TimeTypeDTO> children = new ArrayList<>();


    public TimeTypeDTO(String timeTypes) {
        this.timeTypes = timeTypes;

    }

    public TimeTypeDTO() {
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<TimeTypeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<TimeTypeDTO> children) {
        this.children = children;
    }


    public TimeTypeDTO(BigInteger id, String timeTypes) {
        this.id = id;
        this.timeTypes = timeTypes;
    }

    public TimeTypeDTO(BigInteger id, String timeTypes, String label, String description) {
        this.id = id;
        this.timeTypes = timeTypes;
        this.label = label;
        this.description = description;
    }

    public BigInteger getUpperLevelTimeTypeId() {
        return UpperLevelTimeTypeId;
    }

    public void setUpperLevelTimeTypeId(BigInteger upperLevelTimeTypeId) {
        UpperLevelTimeTypeId = upperLevelTimeTypeId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(String timeTypes) {
        this.timeTypes = timeTypes;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

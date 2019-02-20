package com.kairos.dto.activity.time_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.OrganizationHierarchy;
import com.kairos.enums.TimeTypeEnum;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeTypeDTO {
    private BigInteger id;
    private String timeTypes;
    private String label;
    private String description;
    private BigInteger upperLevelTimeTypeId;
    private boolean selected;
    private List<TimeTypeDTO> children = new ArrayList<>();
    private String backgroundColor;
    private TimeTypeEnum secondLevelType;
    private Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy;

    public TimeTypeDTO() {
    }



    public TimeTypeDTO(String timeTypes, String backgroundColor) {
        this.timeTypes = timeTypes;
        this.backgroundColor = backgroundColor;
    }


    public TimeTypeDTO(BigInteger id, String timeTypes, BigInteger upperLevelTimeTypeId) {
        this.id = id;
        this.timeTypes = timeTypes;
        this.upperLevelTimeTypeId = upperLevelTimeTypeId;
    }

    public TimeTypeDTO(BigInteger id, String timeTypes, String label, String description,String backgroundColor,Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy) {
        this.id = id;
        this.timeTypes = timeTypes;
        this.label = label;
        this.description = description;
        this.backgroundColor=backgroundColor;
        this.activityCanBeCopiedForOrganizationHierarchy = activityCanBeCopiedForOrganizationHierarchy;
    }

    public TimeTypeEnum getSecondLevelType() {
        return secondLevelType;
    }

    public void setSecondLevelType(TimeTypeEnum secondLevelType) {
        this.secondLevelType = secondLevelType;
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

    public BigInteger getUpperLevelTimeTypeId() {
        return upperLevelTimeTypeId;
    }

    public void setUpperLevelTimeTypeId(BigInteger upperLevelTimeTypeId) {
        this.upperLevelTimeTypeId = upperLevelTimeTypeId;
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

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Set<OrganizationHierarchy> getActivityCanBeCopiedForOrganizationHierarchy() {
        return activityCanBeCopiedForOrganizationHierarchy;
    }

    public void setActivityCanBeCopiedForOrganizationHierarchy(Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy) {
        this.activityCanBeCopiedForOrganizationHierarchy = activityCanBeCopiedForOrganizationHierarchy;
    }
}

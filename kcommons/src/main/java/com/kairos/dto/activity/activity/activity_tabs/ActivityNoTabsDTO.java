package com.kairos.dto.activity.activity.activity_tabs;

import java.math.BigInteger;
import java.util.List;

public class ActivityNoTabsDTO {
    private BigInteger id;
    private String name;
    private List<Long> expertises;
    private String description;
    private List<Long> activitySkills;
    private List<Long> employementTypes;
    private long minLength;
    private long maxLength;
    //Its likely a shift setting along with maxDifferentActivities
    private long maxAllocations;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<Long> expertises) {
        this.expertises = expertises;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getActivitySkills() {
        return activitySkills;
    }

    public void setActivitySkills(List<Long> activitySkills) {
        this.activitySkills = activitySkills;
    }

    public List<Long> getEmployementTypes() {
        return employementTypes;
    }

    public void setEmployementTypes(List<Long> employementTypes) {
        this.employementTypes = employementTypes;
    }

    public long getMinLength() {
        return minLength;
    }

    public void setMinLength(long minLength) {
        this.minLength = minLength;
    }

    public long getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(long maxLength) {
        this.maxLength = maxLength;
    }

    public long getMaxAllocations() {
        return maxAllocations;
    }

    public void setMaxAllocations(long maxAllocations) {
        this.maxAllocations = maxAllocations;
    }

    public ActivityNoTabsDTO() {
    }

    public ActivityNoTabsDTO(BigInteger id, String name, List<Long> expertises, String description, List<Long> activitySkills, List<Long> employementTypes, long minLength, long maxLength, long maxAllocations) {
        this.id = id;
        this.name = name;
        this.expertises = expertises;
        this.description = description;
        this.activitySkills = activitySkills;
        this.employementTypes = employementTypes;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.maxAllocations = maxAllocations;
    }
}
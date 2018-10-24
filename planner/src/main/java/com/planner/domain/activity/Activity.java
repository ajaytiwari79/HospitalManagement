package com.planner.domain.activity;

import com.planner.domain.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

public class Activity extends MongoBaseEntity {
    private BigInteger kairosId;
    private String name;
    private List<Long> expertises;
    private String description;
    private List<Long> activitySkills;
    private List<Long> employementTypes;
    private long minLength;
    private long maxLength;
    private long maxAllocations;
    private Long unitId;

    public Activity(String name, List<Long> expertises, String description, List<Long> activitySkills, List<Long> employementTypes, long minLength, long maxLength, long maxAllocations, BigInteger kairosId, Long unitId) {
        this.name = name;
        this.expertises = expertises;
        this.description = description;
        this.activitySkills = activitySkills;
        this.employementTypes = employementTypes;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.maxAllocations = maxAllocations;
        this.unitId = unitId;
        this.kairosId=kairosId;
    }

    public Activity() {

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

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getKairosId() {
        return kairosId;
    }

    public void setKairosId(BigInteger kairosId) {
        this.kairosId = kairosId;
    }
}

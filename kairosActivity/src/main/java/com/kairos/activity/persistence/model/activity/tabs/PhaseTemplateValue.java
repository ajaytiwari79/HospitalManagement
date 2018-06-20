package com.kairos.activity.persistence.model.activity.tabs;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by pavan on 7/2/18.
 */

//This for Activity
public class PhaseTemplateValue implements Serializable {
    private BigInteger phaseId;
    private String name;
    private String description;
    private Set<Long> staffEmployments;
    private boolean eligibleForManagement;

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getStaffEmployments() {
        return staffEmployments=Optional.ofNullable(staffEmployments).orElse(new HashSet<>());
    }

    public void setStaffEmployments(Set<Long> staffEmployments) {
        this.staffEmployments = staffEmployments;
    }

    public boolean isEligibleForManagement() {
        return eligibleForManagement;
    }

    public void setEligibleForManagement(boolean eligibleForManagement) {
        this.eligibleForManagement = eligibleForManagement;
    }

    public PhaseTemplateValue() {

    }

    public PhaseTemplateValue(BigInteger phaseId, String name, String description, Set<Long> staffEmployments, boolean eligibleForManagement) {
        this.phaseId = phaseId;
        this.name = name;
        this.description = description;
        this.staffEmployments = staffEmployments;
        this.eligibleForManagement = eligibleForManagement;
    }
}

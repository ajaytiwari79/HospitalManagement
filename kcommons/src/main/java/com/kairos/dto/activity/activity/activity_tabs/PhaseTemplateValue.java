package com.kairos.dto.activity.activity.activity_tabs;

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
    private List<Long> eligibleEmploymentTypes;
    private boolean eligibleForManagement;
    private boolean staffCanDelete;
    private boolean managementCanDelete;
    private boolean staffCanSell;
    private boolean managementCanSell;
    private int sequence;

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

    public List<Long> getEligibleEmploymentTypes() {
        return eligibleEmploymentTypes =Optional.ofNullable(eligibleEmploymentTypes).orElse(new ArrayList<>());
    }

    public void setEligibleEmploymentTypes(List<Long> eligibleEmploymentTypes) {
        this.eligibleEmploymentTypes = eligibleEmploymentTypes;
    }

    public boolean isEligibleForManagement() {
        return eligibleForManagement;
    }

    public void setEligibleForManagement(boolean eligibleForManagement) {
        this.eligibleForManagement = eligibleForManagement;
    }

    public PhaseTemplateValue() {

    }

    public PhaseTemplateValue(BigInteger phaseId, String name, String description, List<Long> eligibleEmploymentTypes, boolean eligibleForManagement,
                              boolean staffCanDelete, boolean managementCanDelete, boolean staffCanSell, boolean managementCanSell) {
        this.phaseId = phaseId;
        this.name = name;
        this.description = description;
        this.eligibleEmploymentTypes = eligibleEmploymentTypes;
        this.eligibleForManagement = eligibleForManagement;
        this.staffCanDelete = staffCanDelete;
        this.managementCanDelete = managementCanDelete;
        this.staffCanSell = staffCanSell;
        this.managementCanSell = managementCanSell;
    }

    public boolean isStaffCanDelete() {
        return staffCanDelete;
    }

    public void setStaffCanDelete(boolean staffCanDelete) {
        this.staffCanDelete = staffCanDelete;
    }

    public boolean isManagementCanDelete() {
        return managementCanDelete;
    }

    public void setManagementCanDelete(boolean managementCanDelete) {
        this.managementCanDelete = managementCanDelete;
    }

    public boolean isStaffCanSell() {
        return staffCanSell;
    }

    public void setStaffCanSell(boolean staffCanSell) {
        this.staffCanSell = staffCanSell;
    }

    public boolean isManagementCanSell() {
        return managementCanSell;
    }

    public void setManagementCanSell(boolean managementCanSell) {
        this.managementCanSell = managementCanSell;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}

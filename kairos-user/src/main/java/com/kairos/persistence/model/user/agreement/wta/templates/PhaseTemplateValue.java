package com.kairos.persistence.model.user.agreement.wta.templates;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;

/**
 * Created by pavan on 18/1/18.
 */

@NodeEntity
public class PhaseTemplateValue extends UserBaseEntity {
    private int phaseId;
    private String phaseName;
    private short staffValue=0;
    private short managementValue=0;
    private boolean disabled=true;
    private int optionalFrequency=0;
    private boolean optional=false;

    public int getOptionalFrequency() {
        return optionalFrequency;
    }

    public void setOptionalFrequency(int optionalFrequency) {
        this.optionalFrequency = optionalFrequency;
    }



    public int getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(int phaseId) {
        this.phaseId = phaseId;
    }




    public PhaseTemplateValue() {
        //Default Constructor
    }


    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public short getStaffValue() {
        return staffValue;
    }

    public void setStaffValue(short staffValue) {
        this.staffValue = staffValue;
    }


    public short getManagementValue() {
        return managementValue;
    }

    public void setManagementValue(short managementValue) {
        this.managementValue = managementValue;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public String toString() {
        return "PhaseTemplateValue{" +
                "phaseName='" + phaseName + '\'' +
                ", staffValue=" + staffValue +
                ", managementValue=" + managementValue +
                ", disabled=" + disabled +
                ", optional=" + optional +
                '}';
    }
}

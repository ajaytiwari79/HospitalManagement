package com.planning.domain.config;

import com.planning.domain.common.BaseEntity;
import com.planning.enums.SolverConfigPhase;
import org.springframework.data.cassandra.mapping.Table;

import java.util.List;

@Table
public class SolverConfig extends BaseEntity {


    private String name;
    private boolean isTemplate;
    private SolverConfigPhase phase;
    private int hard;
    private int medium;
    private int soft;
    private String parentSolverConfigId;
    private Integer terminationTime;

    public Integer getTerminationTime() {
        return terminationTime;
    }

    public void setTerminationTime(Integer terminationTime) {
        this.terminationTime = terminationTime;
    }

    public String getParentSolverConfigId() {
        return parentSolverConfigId;
    }

    public void setParentSolverConfigId(String parentSolverConfigId) {
        this.parentSolverConfigId = parentSolverConfigId;
    }

    public SolverConfigPhase getPhase() {
        return phase;
    }

    public void setPhase(SolverConfigPhase phase) {
        this.phase = phase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public int getHard() {
        return hard;
    }

    public void setHard(int hard) {
        this.hard = hard;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getSoft() {
        return soft;
    }

    public void setSoft(int soft) {
        this.soft = soft;
    }
}

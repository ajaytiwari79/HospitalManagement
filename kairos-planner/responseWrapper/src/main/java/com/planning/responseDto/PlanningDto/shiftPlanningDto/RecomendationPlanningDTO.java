package com.planning.responseDto.PlanningDto.shiftPlanningDto;

import com.planning.responseDto.commonDto.BaseDTO;

import java.util.Date;
import java.util.List;


public class RecomendationPlanningDTO extends BaseDTO{


    private Date startFrom;
    private Date endTo;
    private Integer noOfRequiredShifts;
    private String solverConfigId;
    private String problemStatus;



    public Date getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(Date startFrom) {
        this.startFrom = startFrom;
    }

    public Date getEndTo() {
        return endTo;
    }

    public void setEndTo(Date endTo) {
        this.endTo = endTo;
    }

    public Integer getNoOfRequiredShifts() {
        return noOfRequiredShifts;
    }

    public void setNoOfRequiredShifts(Integer noOfRequiredShifts) {
        this.noOfRequiredShifts = noOfRequiredShifts;
    }

    public String getSolverConfigId() {
        return solverConfigId;
    }

    public void setSolverConfigId(String solverConfigId) {
        this.solverConfigId = solverConfigId;
    }

    public String getProblemStatus() {
        return problemStatus;
    }

    public void setProblemStatus(String problemStatus) {
        this.problemStatus = problemStatus;
    }
}

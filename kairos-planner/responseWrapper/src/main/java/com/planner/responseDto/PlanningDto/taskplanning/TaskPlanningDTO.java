package com.planner.responseDto.PlanningDto.taskplanning;

import com.planner.responseDto.commonDto.BaseDTO;

import java.util.Date;

public class TaskPlanningDTO extends BaseDTO{
    

    private Date startDateTime;
    private Date endDateTime;
    private String planningProblemStatus;
    private String callBackUrl;
    private String solverConfigId;

    public String getSolverConfigId() {
        return solverConfigId;
    }

    public void setSolverConfigId(String solverConfigId) {
        this.solverConfigId = solverConfigId;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }


    public String getPlanningProblemStatus() {
        return planningProblemStatus;
    }

    public void setPlanningProblemStatus(String planningProblemStatus) {
        this.planningProblemStatus = planningProblemStatus;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

}

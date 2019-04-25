package com.planner.domain.taskPlanning;

import com.planner.domain.common.BaseEntity;
import com.planner.enums.PlanningStatus;
//import org.springframework.data.cassandra.core.mapping.Table;

//@Table
public class PlanningProblem extends BaseEntity {

	private String callBackUrl;
	private String planningId;
	private PlanningStatus status;
	private String problemXml;
	private String solutionXml;
	private String solverConfigId;


	public String getSolverConfigId() {
		return solverConfigId;
	}

	public void setSolverConfigId(String solverConfigId) {
		this.solverConfigId = solverConfigId;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getProblemXml() {
		return problemXml;
	}

	public void setProblemXml(String problemXml) {
		this.problemXml = problemXml;
	}

	public String getSolutionXml() {
		return solutionXml;
	}

	public void setSolutionXml(String solutionXml) {
		this.solutionXml = solutionXml;
	}

	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String planningId) {
		this.planningId = planningId;
	}
	public PlanningStatus getStatus() {
		return status;
	}
	public void setStatus(PlanningStatus status) {
		this.status = status;
	}
	
	
}

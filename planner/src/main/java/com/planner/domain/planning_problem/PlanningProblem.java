package com.planner.domain.planning_problem;

import com.kairos.enums.planning_problem.PlanningProblemType;
import com.planner.domain.common.MongoBaseEntity;
import com.planner.enums.PlanningProblemStatus;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Document
public class PlanningProblem extends MongoBaseEntity{
    private String name;
    private String description;
    private PlanningProblemType type;
    private Date planningStartDate;
    private Date planningEndDate;
    private PlanningProblemStatus status;
    private String problemFileName;
    private String solutionFileName;
    private Long countryId;

    public PlanningProblem() {
    }

    public PlanningProblem(String name, String description, PlanningProblemType type,Long countryId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.countryId = countryId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public PlanningProblemType getType() {
        return type;
    }

    public void setType(PlanningProblemType type) {
        this.type = type;
    }


    public String getProblemFileName() {
        return problemFileName;
    }

    public void setProblemFileName(String problemFileName) {
        this.problemFileName = problemFileName;
    }

    public String getSolutionFileName() {
        return solutionFileName;
    }

    public void setSolutionFileName(String solutionFileName) {
        this.solutionFileName = solutionFileName;
    }

    public PlanningProblemStatus getStatus() {

        return status;
    }

    public void setStatus(PlanningProblemStatus status) {
        this.status = status;
    }

    public Date getPlanningStartDate() {
        return planningStartDate;
    }

    public void setPlanningStartDate(Date planningStartDate) {
        this.planningStartDate = planningStartDate;
    }

    public Date getPlanningEndDate() {
        return planningEndDate;
    }

    public void setPlanningEndDate(Date planningEndDate) {
        this.planningEndDate = planningEndDate;
    }
}

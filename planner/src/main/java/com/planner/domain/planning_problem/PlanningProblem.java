package com.planner.domain.planning_problem;

import com.kairos.enums.planning_problem.PlanningProblemType;
import com.planner.domain.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PlanningProblem extends MongoBaseEntity{
    private String name;
    private String description;
    private PlanningProblemType type;

    public PlanningProblem() {
    }

    public PlanningProblem(String name, String description, PlanningProblemType type) {
        this.name = name;
        this.description = description;
        this.type = type;
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
}

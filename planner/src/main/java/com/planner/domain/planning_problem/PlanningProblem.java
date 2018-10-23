package com.planner.domain.planning_problem;

import com.planner.domain.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PlanningProblem extends MongoBaseEntity{
    private String name;
    private String description;

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
}

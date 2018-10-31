package com.planner.domain.planning_problem;

import com.planner.domain.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PlanningProblem extends MongoBaseEntity{
    private String name;
    private String description;
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

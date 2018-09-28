package com.planner.domain.constarints;

import com.planner.domain.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Constraint extends MongoBaseEntity{

    private String name;
    private String description;

    /*=============================Setters/Getters======================================*/


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

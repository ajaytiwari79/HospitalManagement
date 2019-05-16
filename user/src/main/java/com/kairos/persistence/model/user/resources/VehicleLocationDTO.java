package com.kairos.persistence.model.user.resources;

import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;

@QueryResult
public class VehicleLocationDTO {

    private Long id;
    @NotNull(message = ERROR_NAME_NOTNULL)
    private String name;
    private String description;

    public VehicleLocationDTO() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}

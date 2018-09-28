package com.kairos.dto.planner.constarints;

import java.math.BigInteger;

public class ConstraintDTO {
    private BigInteger id;
    private String name;
    private String description;
    //===========================================================================

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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

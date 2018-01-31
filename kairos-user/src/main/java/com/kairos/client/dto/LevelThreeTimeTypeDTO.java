package com.kairos.client.dto;

import java.math.BigInteger;

public class LevelThreeTimeTypeDTO {

    private BigInteger id;
    private String timeTypes;
    private String upperLevelTimeTypeId;
    private String name;
    private String description;

    public LevelThreeTimeTypeDTO(BigInteger id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public LevelThreeTimeTypeDTO() {
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(String timeTypes) {
        this.timeTypes = timeTypes;
    }

    public String getUpperLevelTimeTypeId() {
        return upperLevelTimeTypeId;
    }

    public void setUpperLevelTimeTypeId(String upperLevelTimeTypeId) {
        this.upperLevelTimeTypeId = upperLevelTimeTypeId;
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

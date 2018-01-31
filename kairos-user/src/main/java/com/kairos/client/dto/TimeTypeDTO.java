package com.kairos.client.dto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TimeTypeDTO {

    private BigInteger id;
    private String timeTypes;
    private String upperLevelTimeTypeId;
    private String name;
    private String description;
    private List<LevelOneTimeTypeDTO> level1 = new ArrayList<>();

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

    public List<LevelOneTimeTypeDTO> getLevel1() {
        return level1;
    }

    public void setLevel1(List<LevelOneTimeTypeDTO> level1) {
        this.level1 = level1;
    }
}

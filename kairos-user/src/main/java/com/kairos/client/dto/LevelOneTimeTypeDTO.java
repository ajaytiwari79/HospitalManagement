package com.kairos.client.dto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class LevelOneTimeTypeDTO {

    private BigInteger id;
    private String timeTypes;
    private String upperLevelTimeTypeId;
    private String name;
    private String description;
    //private boolean includeTimeBank;
    private List<LevelTwoTimeTypeDTO> level2 = new ArrayList<>();

    public LevelOneTimeTypeDTO() {
    }

    public LevelOneTimeTypeDTO(BigInteger id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public List<LevelTwoTimeTypeDTO> getLevel2() {
        return level2;
    }

    public void setLevel2(List<LevelTwoTimeTypeDTO> level2) {
        this.level2 = level2;
    }
}

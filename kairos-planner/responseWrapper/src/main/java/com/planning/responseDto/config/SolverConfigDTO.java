package com.planning.responseDto.config;

import com.planning.responseDto.commonDto.BaseDTO;

import java.util.List;

public class SolverConfigDTO extends BaseDTO{

    private String name;
    private boolean isTemplate;
    private Integer hardLevel;
    private Integer mediumLevel;
    private Integer softLevel;
    private List<ConstraintDTO> constraintDTOList;
    private List<CategoryDTO> categoryDTOS;
    private String phase;
    private Integer terminationTime;

    public Integer getTerminationTime() {
        return terminationTime;
    }

    public void setTerminationTime(Integer terminationTime) {
        this.terminationTime = terminationTime;
    }

    public List<CategoryDTO> getCategoryDTOS() {
        return categoryDTOS;
    }

    public void setCategoryDTOS(List<CategoryDTO> categoryDTOS) {
        this.categoryDTOS = categoryDTOS;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public Integer getHardLevel() {
        return hardLevel;
    }

    public void setHardLevel(Integer hardLevel) {
        this.hardLevel = hardLevel;
    }

    public Integer getMediumLevel() {
        return mediumLevel;
    }

    public void setMediumLevel(Integer mediumLevel) {
        this.mediumLevel = mediumLevel;
    }

    public Integer getSoftLevel() {
        return softLevel;
    }

    public void setSoftLevel(Integer softLevel) {
        this.softLevel = softLevel;
    }

    public List<ConstraintDTO> getConstraintDTOList() {
        return constraintDTOList;
    }

    public void setConstraintDTOList(List<ConstraintDTO> constraintDTOList) {
        this.constraintDTOList = constraintDTOList;
    }
}

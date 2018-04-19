package com.planner.domain.workTimeAgreement;

import com.planner.domain.common.BaseEntity;

public class WorkingTimeAgreement extends BaseEntity {

    private String name;
    private String description;
    private int value;

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

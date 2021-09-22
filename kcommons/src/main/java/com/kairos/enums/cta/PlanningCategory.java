package com.kairos.enums.cta;

import java.io.Serializable;

public enum  PlanningCategory implements Serializable {
    DEVIATION_FROM_PLANNED("Deviation from Planned"),PLANNABLE("Plannable"),TASK_SPECIFIC("Task Specific");
    private String category;
    PlanningCategory(String category){
        this.category=category;
    }
}

package com.kairos.persistence.model.agreement.cta;

public enum  PlanningCategory {
    DEVIATION_FROM_PLANNED("Deviation from Planned"),PLANNABLE("Plannable"),TASK_SPECIFIC("Task Specific");
    private String category;
    PlanningCategory(String category){
        this.category=category;
    }
}

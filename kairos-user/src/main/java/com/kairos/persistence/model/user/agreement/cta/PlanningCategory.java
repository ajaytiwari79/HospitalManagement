package com.kairos.persistence.model.user.agreement.cta;

public enum  PlanningCategory {
    DEVIATION_FROM_PLANED("Deviation from Planed"),PLANNABLE("Plannable"),TASK_SPECIFIC("Task Specific");
    private String category;
    PlanningCategory(String category){
        this.category=category;
    }
}

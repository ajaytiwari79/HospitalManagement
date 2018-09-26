package com.kairos.dto.activity.cta;

public enum  BudgetType {
    ACTIVITY_COST("Activity cost"),OVERHEAD_COST("Overhead Cost"),REVENUE_TO_UNIT("Revenue to Unit");
    String budgetType;
    BudgetType(String budgetType){
        this.budgetType=budgetType;
    }

}

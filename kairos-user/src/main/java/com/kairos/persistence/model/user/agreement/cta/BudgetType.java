package com.kairos.persistence.model.user.agreement.cta;

public enum  BudgetType {
    ACTVITY_COST("Activity cost"),OVERHEAD("Overhead Cost"),REVENUE_TO_UNIT("Revenue to Unit");
    String budgetType;
    BudgetType(String budgetType){
        this.budgetType=budgetType;
    }

}

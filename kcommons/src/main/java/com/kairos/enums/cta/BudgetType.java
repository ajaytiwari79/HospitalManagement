package com.kairos.enums.cta;

import java.io.Serializable;

public enum  BudgetType implements Serializable {
    ACTIVITY_COST("Activity cost"),OVERHEAD_COST("Overhead Cost"),REVENUE_TO_UNIT("Revenue to Unit");
    String budgetType;
    BudgetType(String budgetType){
        this.budgetType=budgetType;
    }

}

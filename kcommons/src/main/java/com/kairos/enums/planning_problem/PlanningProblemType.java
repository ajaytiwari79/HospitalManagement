package com.kairos.enums.planning_problem;

import com.kairos.enums.MasterDataTypeEnum;

public enum PlanningProblemType {
    SHIFT_PLANNING("Shift planning"),TASK_PLANNING("Task planning"),VEHICLE_ROUTE_PLANNING("Vehicle route planning");

    private String value;

    PlanningProblemType(String value) {
        this.value = value;
    }

    public MasterDataTypeEnum getByValue(String value){
        for(MasterDataTypeEnum masterDataTypeEnum : MasterDataTypeEnum.values()){
            if(masterDataTypeEnum.value.equals(value)){
                return masterDataTypeEnum;
            }
        }
        return null;
    }
}

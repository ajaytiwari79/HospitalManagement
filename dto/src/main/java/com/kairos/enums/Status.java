package com.kairos.enums;
public enum Status{

    VISITATED("Visitated"), //When demand is created
    GENERATED("Generated"), //When demand dragged&dropped in planner
    UPDATED("Updated"),     //When Demand is updated (assign this status, when demand's status is generated while updating it)
    PLANNED("Planned"),     //When any of the task is planned
    DELIVERED("Delivered"); //When any of the task is delivered


    public String value;

    Status(String value) {
        this.value = value;
    }

    public static Status getByValue(String value){
        for(Status status : Status.values()){
            if(status.value.equals(value)){
                return status;
            }
        }
        return null;
    }
}
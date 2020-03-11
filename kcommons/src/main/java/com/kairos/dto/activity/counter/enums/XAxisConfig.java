package com.kairos.dto.activity.counter.enums;

public enum XAxisConfig {
    HOURS("Hours"),COUNT("Count"),PERCENTAGE("Percentage %"),PERCENTAGE_OF_HOURS("Percentage of hours"),PERCENTAGE_OF_TIMES("Percentage of Times"),VARIABLE_COST("Variable Cost"),BREAK_INTERRUPT("Break Interrupt"),AVERAGE("Average"),AVERAGE_PER_DAY("Average Per Day"),PAY_GRADE_VALUE("Pay Grade Value"),AGE("Age") ;


    private String displayValue;
    private XAxisConfig(String displayValue){
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}

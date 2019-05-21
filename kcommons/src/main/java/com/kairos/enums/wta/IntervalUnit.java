package com.kairos.enums.wta;

public enum IntervalUnit {
    DAYS("Days"),WEEKS("Weeks"), MONTHS("Months"),YEARS("Years"),NEXT("Next"), CURRENT("Current"), LAST("Last");
    private String value;
    IntervalUnit(String value){
        this.value=value;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}

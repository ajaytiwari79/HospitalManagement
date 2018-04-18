package com.kairos.shiftplanning.domain.cta;

public enum CompensationType {
    MINUTES("Minutes"),PERCENTAGE("Percentage"),FIXED("Fixed");

    private String value;

    private CompensationType(String value) {
        this.value = value;
    }

    public String toValue() {
        return value;
    }

    public static CompensationType getEnumByString(String status) {
        for (CompensationType is : CompensationType.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }


}

package com.kairos.activity.enums.PriorityGroup;

public enum Priority {
    ONE(1),TWO(2),THREE(3),FOUR(4);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

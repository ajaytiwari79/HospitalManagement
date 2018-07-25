package com.kairos.persistence.model.common;

/**
 * Created by oodles on 11/4/17.
 */
public enum SequenceName {

    TASK("TASK"),
    DAY_HISTORY("DAY_HISTORY"),
    VEHICLE_HISTORY("VEHICLE_HISTORY"),
    TASK_TYPE("TASK_TYPE"),
    TASK_DEMAND("TASK_DEMAND"),
    KEY_HISTORY("KEY_HISTORY"),
    TASK_HISTORY("TASK_HISTORY"),
    TASK_PACKAGE("TASK_PACKAGE"),
    JOB_DETAILS("JOB_DETAILS"),
    CLIENT_EXCEPTION_TYPE("CLIENT_EXCEPTION_TYPE"),
    BEACON("BEACON"),
    ACTUAL_PLANING_TASK("ACTUAL_PLANING_TASK"),
    KAIROS_SEQUENCE("KAIROS_SEQUENCE"),
    MAP_POINTER("MAP_POINTER"),
    TASK_REPORT("TASK_REPORT");

    public String value;

    SequenceName(String value) {
        this.value = value;
    }

    public static SequenceName getByValue(String value) {
        for (SequenceName sequenceName : SequenceName.values()) {
            if (sequenceName.value.equals(value)) {
                return sequenceName;
            }
        }
        return null;
    }
}

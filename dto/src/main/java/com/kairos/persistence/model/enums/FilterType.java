package com.kairos.persistence.model.enums;

/**
 * Created by prerna on 30/4/18.
 */
public enum FilterType {

    EMPLOYMENT_TYPE("Employment Type"), EXPERTISE("Expertise"), STAFF_STATUS("Status"), GENDER("Gender"), ENGINEER_TYPE("Engineer Type");

    public String value;

    FilterType(String value) {
        this.value = value;
    }
}

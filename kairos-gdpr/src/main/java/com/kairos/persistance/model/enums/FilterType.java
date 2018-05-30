package com.kairos.persistance.model.enums;

/*
 *
 * created for filter types show on screen
 *
 *
 * */

public enum FilterType {


    ORGANIZATION_TYPES("organizationTypes"), ORGANIZATION_SUB_TYPES("organizationSubTypes"), ORGANIZATION_SERVICES("organizationServices"), ORGANIZATION_SUB_SERVICES("organizationSubServices"),ACCOUNT_TYPES("accountTypes");

    public String value;

    FilterType(String value) {
        this.value = value;
    }


}

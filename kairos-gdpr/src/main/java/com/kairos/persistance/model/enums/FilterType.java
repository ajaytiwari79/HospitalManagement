package com.kairos.persistance.model.enums;

/*
 *
 * created for filter types show on screen
 *
 *
 * */
public enum FilterType {


    ORGANIZATION_TYPES("organization_types"), ORGANIZATION_SUB_TYPES("organization_sub_types"), ORGANIZATION_SERVICES("organization_services"), ORGANIZATION_SUB_SERVICES("organization_sub_services");

    public String value;

    FilterType(String value) {
        this.value = value;
    }


}

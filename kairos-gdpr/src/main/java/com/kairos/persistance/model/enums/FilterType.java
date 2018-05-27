package com.kairos.persistance.model.enums;

/*
 *
 * created for filter types show on screen
 *
 *
 * */
public enum FilterType {


    ORGANIZATION_TYPE("Organization Type"), ORGANIZATION_SUB_TYPE("Organization Sub Type "), ORGANIZATION_SERVICE("Organization Service"), ORGANIZATION_SUB_SERVICE("Organization Sub Services");

    public String value;

    FilterType(String value) {
        this.value = value;
    }


}

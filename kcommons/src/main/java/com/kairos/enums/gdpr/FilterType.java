package com.kairos.enums.gdpr;

/*
 *
 * created for filter types show on screen
 * Filter type ORGANIZATION_TYPES,ORGANIZATION_SUB_TYPES, ORGANIZATION_SERVICES,ORGANIZATION_SUB_SERVICES represent field in domain of Processing activity ,clause and Asset
 * */

public enum FilterType {


    ORGANIZATION_TYPES("Organization Types"), ORGANIZATION_SUB_TYPES("Organization Sub Types"), ORGANIZATION_SERVICES("Service Types"),
    ORGANIZATION_SUB_SERVICES("Service Sub Types"),ACCOUNT_TYPES("Account Types");

    public String value;
    FilterType(String value) {
        this.value = value;
    }


}

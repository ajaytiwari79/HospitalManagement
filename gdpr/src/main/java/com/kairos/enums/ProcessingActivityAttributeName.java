package com.kairos.enums;

public enum ProcessingActivityAttributeName {

NAME("Name"),DESCRIPTION("Description"),MANAGINGDEPARTMENT("Managing Department"), PROCESSOWNER("Process Owner"),PROCESSINGPURPOSES("Processing Purpose"),DATASOURCES("Data Source"),
    TRANSFERMETHODS("Transfer method"),ACCESSORPARTIES("Accessor Parties"),PROCESSINGLEGALBASIS("Processing Legal Basis"),RESPONSIBILITYTYPE("Responsibility Type"),CONTROLLERCONTACTINFO("Controller Contact Info")
    ,DPOCONTACTINFO("DPO Contact Info"),JOINTCONTROLLERCONTACTINFO("Joint Controller Info"),MINDATASUBJECTVOLUME("Minimum Data Subject Volume"),
    MAXDATASUBJECTVOLUME("Max Data Subject Volume"),DATARETENTIONPERIOD("Data Retention Period");

    public String value;
    ProcessingActivityAttributeName(String value) {
        this.value = value;
    }

}

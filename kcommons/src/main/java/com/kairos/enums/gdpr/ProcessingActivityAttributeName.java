package com.kairos.enums.gdpr;

public enum ProcessingActivityAttributeName {

NAME("Name"),DESCRIPTION("Description"),MANAGING_DEPARTMENT("Managing Department"), PROCESS_OWNER("Process Owner"),PROCESSING_PURPOSES("Processing Purpose"),DATA_SOURCES("Data Source"),
    TRANSFER_METHOD("Transfer method"),ACCESSOR_PARTY("Accessor Parties"),PROCESSING_LEGAL_BASIS("Processing Legal Basis"),RESPONSIBILITY_TYPE("Responsibility Type"),CONTROLLER_CONTACT_INFO("Controller Contact Info")
    ,DPO_CONTACT_INFO("DPO Contact Info"),JOINT_CONTROLLER_CONTACT_INFO("Joint Controller Info"),MIN_DATA_SUBJECT_VOLUME("Minimum Data Subject Volume"),
    MAX_DATA_SUBJECT_VOLUME("Max Data Subject Volume"),DATA_RETENTION_PERIOD("Data Retention Period");

    public String value;
    ProcessingActivityAttributeName(String value) {
        this.value = value;
    }

}

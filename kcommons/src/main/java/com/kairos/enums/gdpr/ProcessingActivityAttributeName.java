package com.kairos.enums.gdpr;

public enum ProcessingActivityAttributeName {

NAME("name"),DESCRIPTION("description"),PROCESSING_PURPOSES("processingPurposes"),DATA_SOURCES("dataSources"),
    TRANSFER_METHOD("transferMethods"),ACCESSOR_PARTY("accessorParties"),PROCESSING_LEGAL_BASIS("processingLegalBasis"),RESPONSIBILITY_TYPE("responsibilityType"),CONTROLLER_CONTACT_INFO("controllerContactInfo")
    ,DPO_CONTACT_INFO("dpoContactInfo"),JOINT_CONTROLLER_CONTACT_INFO("jointControllerContactInfo"),MIN_DATA_SUBJECT_VOLUME("minDataSubjectVolume"),
    MAX_DATA_SUBJECT_VOLUME("maxDataSubjectVolume"),DATA_RETENTION_PERIOD("dataRetentionPeriod"),ASSETS("assets");

    public String value;
    ProcessingActivityAttributeName(String value) {
        this.value = value;
    }

}

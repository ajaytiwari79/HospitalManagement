package com.kairos.enums;

public enum AssetAttributeName {


    NAME("Name"), DESCRIPTION("Description"), HOSTING_LOCATION("Hosting Location"), MANAGING_DEPARTMENT("Managing Department"), ASSET_OWNER("Asset Owner"), STORAGE_FORMAT("Storage Format"),
    ORGANIZATION_SECURITY_MEASURES("Organization Security Measures"), TECHNICAL_SECURITY_MEASURES("Technical Security Measure"), PROCESSING_ACTIVITIES("Processing Activities"), HOSTING_PROVIDER("Hosting Provider"),
    HOSTINGTYPE("Hosting Type"), DATADISPOSAL("Data Disposal"), ASSET_TYPE("Asset Type"), ASSET_SUB_TYPE("Sub Asset Type"), MIN_DATA_SUBJECT_VOLUME("Minimum Data Subject Volume"),
    MAX_DATA_SUBJECT_VOLUME("Max Data Subject Volume"), DATA_RETENTION_PERIOD("Data Retention Period");

    public String value;
    AssetAttributeName(String value) {
        this.value = value;
    }

}

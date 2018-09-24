package com.kairos.enums.gdpr;

public enum AssetAttributeName {


    NAME("name"), DESCRIPTION("description"), HOSTING_LOCATION("hostingLocation"), MANAGING_DEPARTMENT("managingDepartment"), ASSET_OWNER("assetOwner"), STORAGE_FORMAT("storageFormats"),
    ORGANIZATION_SECURITY_MEASURES("orgSecurityMeasures"), TECHNICAL_SECURITY_MEASURES("technicalSecurityMeasures"), PROCESSING_ACTIVITIES("Processing Activities"), HOSTING_PROVIDER("hostingProvider"),
    HOSTING_TYPE("hostingType"), DATA_DISPOSAL("dataDisposal"), ASSET_TYPE("assetType"), ASSET_SUB_TYPE("assetSubTypes"), MIN_DATA_SUBJECT_VOLUME("minDataSubjectVolume"),
    MAX_DATA_SUBJECT_VOLUME("maxDataSubjectVolume"), DATA_RETENTION_PERIOD("dataRetentionPeriod");

    public String value;
    AssetAttributeName(String value) {
        this.value = value;
    }

}

package com.kairos.enums.gdpr;

public enum AssetAttributeName {


    NAME("name"), DESCRIPTION("description"), HOSTING_LOCATION("hostingLocation"), STORAGE_FORMAT("storageFormats"),
    ORGANIZATION_SECURITY_MEASURES("orgSecurityMeasures"), TECHNICAL_SECURITY_MEASURES("technicalSecurityMeasures"), HOSTING_PROVIDER("hostingProvider"),
    HOSTING_TYPE("hostingType"), DATA_DISPOSAL("dataDisposal"), ASSET_TYPE("assetType"), ASSET_SUB_TYPE("subAssetType"),  DATA_RETENTION_PERIOD("dataRetentionPeriod");

    public String value;
    AssetAttributeName(String value) {
        this.value = value;
    }

}

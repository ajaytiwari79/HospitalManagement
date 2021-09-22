package com.kairos.enums.gdpr;

import java.io.Serializable;

public enum AssetAttributeName implements Serializable {


    NAME("name"), DESCRIPTION("description"), HOSTING_LOCATION("hostingLocation"), STORAGE_FORMAT("storageFormats"),
    ORGANIZATION_SECURITY_MEASURES("orgSecurityMeasures"), TECHNICAL_SECURITY_MEASURES("technicalSecurityMeasures"), HOSTING_PROVIDER("hostingProvider"),
    HOSTING_TYPE("hostingType"), DATA_DISPOSAL("dataDisposal"), ASSET_TYPE("assetType"), ASSET_SUB_TYPE("subAssetType"),  DATA_RETENTION_PERIOD("dataRetentionPeriod");

    public String value;
    AssetAttributeName(String value) {
        this.value = value;
    }

}

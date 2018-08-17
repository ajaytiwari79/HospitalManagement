package com.kairos.enums;

public enum AssetAttributeName {


    NAME("Name"), DESCRIPTION("Description"), HOSTINGLOCATION("Hosting Location"), MANAGINGDEPARTMENT("Managing Department"), ASSETOWNER("Asset Owner"), STORAGEFORMATS("Storage Format"),
    ORGSECURITYMEASURES("Organization Security Measures"), TECHNICALSECURITYMEASURES("Technical Security Measure"), PROCESSINGACTIVITIES("Processing Activities"), HOSTINGPROVIDER("Hosting Provider"),
    HOSTINGTYPE("Hosting Type"), DATADISPOSAL("Data Disposal"), ASSETTYPE("Asset Type"), ASSETSUBTYPES("Sub Asset Type"), MINDATASUBJECTVOLUME("Minimum Data Subject Volume"),
    MAXDATASUBJECTVOLUME("Max Data Subject Volume"), DATARETENTIONPERIOD("Data Retention Period");

    public String value;
    AssetAttributeName(String value) {
        this.value = value;
    }

}

package com.kairos.enums;

public enum  QuestionnaireType
{

    ASSET_TYPE("Asset Type"),  VENDOR("Vendor"),  QUESTION_TYPE_3("MasterQuestion Type"),  GENRAL("Genral");

    public String value;
    QuestionnaireType(String value) {
        this.value = value;
    }

}

package com.kairos.enums.gdpr;

public  enum QuestionnaireTemplateType
{

    ASSET_TYPE("asset_type"),  VENDOR("vendor"),  PROCESSING_ACTIVITY("processing_activity"),  GENERAL("general"),Risk("risk");

    public String value;
    QuestionnaireTemplateType(String value) {
        this.value = value;
    }


}

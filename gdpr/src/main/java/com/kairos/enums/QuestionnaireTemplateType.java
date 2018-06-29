package com.kairos.enums;

public enum QuestionnaireTemplateType
{

    ASSET_TYPE("asset"),  VENDOR("vendor"),  PROCESSING_ACTIVITY("processing-activity"),  GENERAL("general");

    public String value;
    QuestionnaireTemplateType(String value) {
        this.value = value;
    }

}

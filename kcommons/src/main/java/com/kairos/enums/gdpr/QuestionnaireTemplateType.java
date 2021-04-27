package com.kairos.enums.gdpr;

import java.io.Serializable;

public  enum QuestionnaireTemplateType implements Serializable {

    ASSET_TYPE("asset_type"),  VENDOR("vendor"),  PROCESSING_ACTIVITY("processing_activity"),  GENERAL("general"), RISK("risk");

    public String value;
    QuestionnaireTemplateType(String value) {
        this.value = value;
    }


}

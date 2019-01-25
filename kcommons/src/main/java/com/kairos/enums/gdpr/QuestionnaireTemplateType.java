package com.kairos.enums.gdpr;

public  enum QuestionnaireTemplateType
{

    ASSET_TYPE("asset_type"),  VENDOR("vendor"),  PROCESSING_ACTIVITY("processing_activity"),  GENERAL("general"), RISK("risk");

    public String value;
    QuestionnaireTemplateType(String value) {
        this.value = value;
    }

    public static QuestionnaireTemplateType getQuestionnaireTemplateType(final String value) {

        for (QuestionnaireTemplateType templateType : QuestionnaireTemplateType.values()) {
            if (value.equals(templateType.toString())) {
                return templateType;
            }
        }
        return null;
    }


}
